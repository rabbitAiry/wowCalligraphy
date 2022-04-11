package com.airy.wowcalligraphy.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class CharacterDrawView extends View {
    private int paddingLeft, paddingTop;
    private Bitmap characterBitmap, drawBitmap;
    private Canvas drawCanvas;
    private Paint paint, transparentPaint;
    private Path path;
    private Handler handler = new Handler();
    private float preX, preY;
    private float filledRate = -1, overFilledRate = -1;
    private EvaluateListener evaluateListener;

    public interface EvaluateListener{
        void onEvaluated(float filledRate, float overFilledRate);
    }

    public CharacterDrawView(Context context) {
        super(context);
    }

    public CharacterDrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CharacterDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CharacterDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    void initPaint(){
        paint = new Paint(Paint.DITHER_FLAG);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);//设置填充方式为描边
        paint.setStrokeJoin(Paint.Join.ROUND);//设置笔刷转弯处的连接风格
        paint.setStrokeCap(Paint.Cap.ROUND);//设置笔刷的图形样式(体现在线的端点上)
        paint.setStrokeWidth(72);//设置默认笔触的宽度为72像素
        paint.setAntiAlias(true);//设置抗锯齿效果
        paint.setDither(true);//使用抖动效果
        transparentPaint = new Paint();
        transparentPaint.setAlpha(160);
        transparentPaint.setStyle(Paint.Style.STROKE);//设置填充方式为描边
        transparentPaint.setStrokeJoin(Paint.Join.ROUND);//设置笔刷转弯处的连接风格
        transparentPaint.setStrokeCap(Paint.Cap.ROUND);//设置笔刷的图形样式(体现在线的端点上)
        transparentPaint.setStrokeWidth(72);//设置默认笔触的宽度为72像素
        transparentPaint.setAntiAlias(true);//设置抗锯齿效果
        transparentPaint.setDither(true);//使用抖动效果
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(characterBitmap==null)return;
        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        canvas.drawBitmap(characterBitmap, paddingLeft, paddingTop, null);
        canvas.drawBitmap(drawBitmap, 0, 0, transparentPaint);
        canvas.drawPath(path, transparentPaint);
        canvas.save();
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(characterBitmap==null)return false;
        //获取触摸事件发生的位置
        float x=event.getX();
        float y=event.getY();
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //将绘图的起始点移到(x,y)坐标点的位置
                path.moveTo(x, y);
                preX=x;
                preY=y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx=Math.abs(x-preX);
                float dy=Math.abs(y-preY);
                if(dx>5||dy>5){
                    //.quadTo贝塞尔曲线，实现平滑曲线(对比lineTo)
                    //x1，y1为控制点的坐标值，x2，y2为终点的坐标值
                    path.quadTo(preX, preY, (x+preX)/2, (y+preY)/2);
                    preX=x;
                    preY=y;
                }
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(path, paint);//绘制路径
                path.reset();
                evaluatePaint();
                break;
        }
        invalidate();
        return true;//返回true,表明处理方法已经处理该事件
    }

    public void setBitMapAsBackground(Bitmap src){
        int viewHeight = getHeight(), viewWidth = getWidth();
        int height = src.getHeight(), width = src.getWidth();
        float heightRate = (float)viewHeight/height, widthRate = (float)viewWidth/width;
        float rate = Math.min(heightRate, widthRate);
        int finalWidth = (int)rate*width, finalHeight = (int)rate*height;

        characterBitmap = Bitmap.createScaledBitmap(src, finalWidth, finalHeight, true);
        drawBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(drawBitmap);
        drawCanvas.drawColor(Color.WHITE);
        path = new Path();
        initPaint();
        invalidate();
    }

    byte[] bitmapToByteArray(Bitmap bitmap){
        int width = bitmap.getWidth();//原图像宽度
        int height = bitmap.getHeight();//原图像高度
        int color;//用来存储某个像素点的颜色值
        int r, g, b, a;//红，绿，蓝，透明度
        //创建空白图像，宽度等于原图宽度，高度等于原图高度，用ARGB_8888渲染，这个不用了解，这样写就行了
        Bitmap bmp = Bitmap.createBitmap(width, height
                , Bitmap.Config.ARGB_8888);

        int[] oldPx = new int[width * height];//用来存储原图每个像素点的颜色信息
        byte[] res = new byte[width * height];//用来处理处理之后的每个像素点的颜色信息
        /**
         * 第一个参数oldPix[]:用来接收（存储）bm这个图像中像素点颜色信息的数组
         * 第二个参数offset:oldPix[]数组中第一个接收颜色信息的下标值
         * 第三个参数width:在行之间跳过像素的条目数，必须大于等于图像每行的像素数
         * 第四个参数x:从图像bm中读取的第一个像素的横坐标
         * 第五个参数y:从图像bm中读取的第一个像素的纵坐标
         * 第六个参数width:每行需要读取的像素个数
         * 第七个参数height:需要读取的行总数
         */
        bitmap.getPixels(oldPx, 0, width, 0, 0, width, height);//获取原图中的像素信息

        for (int i = 0; i < width * height; i++) {//循环处理图像中每个像素点的颜色值
            color = oldPx[i];//取得某个点的像素值
            r = Color.red(color);//取得此像素点的r(红色)分量
            g = Color.green(color);//取得此像素点的g(绿色)分量
            b = Color.blue(color);//取得此像素点的b(蓝色分量)
            a = Color.alpha(color);//取得此像素点的a通道值

            //此公式将r,g,b运算获得灰度值，经验公式不需要理解
            int gray = (int) ((float) r * 0.3 + (float) g * 0.59 + (float) b * 0.11);
            if (gray > 0) {
                res[i] = 1;
            }else{
                res[i] = 0;
            }
        }
        Log.d("bitmap", "ByteArray.length:"+res.length);
        return res;
    }

    public void setEvaluateListener(EvaluateListener listener){
        evaluateListener = listener;
    }

    public void evaluatePaint(){
        new Thread(() -> {
            int paintCnt = 0, drawCnt = 0, fillCnt = 0, overFillCnt = 0;
            byte[] drawArr = bitmapToByteArray(drawBitmap);
            byte[] characterArr = bitmapToByteArray(characterBitmap);
            int drawHeight = drawBitmap.getHeight(), drawWidth = drawBitmap.getWidth();
            int characterHeight = characterBitmap.getHeight(), characterWidth = characterBitmap.getWidth();
            for (int i = 0; i < drawArr.length/drawHeight; i++) {
                for (int j = 0; j < drawArr.length/drawWidth; j++) {
                    int mappedIdx = i*drawHeight+j;
                    int mappedIdxCharacter = -1;
                    if(i>=paddingTop && j>=paddingLeft && i-paddingTop<characterHeight && j-paddingLeft<characterWidth){
                        mappedIdxCharacter = (i-paddingTop)*characterWidth+(j-paddingLeft);
                        if(characterArr[mappedIdxCharacter]==0)paintCnt++;
                        if(characterArr[mappedIdxCharacter]==0 && drawArr[mappedIdx]==0)fillCnt++;
                        if(characterArr[mappedIdxCharacter]!=0 && drawArr[mappedIdx]==0)overFillCnt++;
                    }
                    if(drawArr[mappedIdx]==0 && mappedIdxCharacter==-1)overFillCnt++;
                    if(drawArr[mappedIdx]==0)drawCnt++;
                }
            }

            Log.d("bitmap", "paintCnt = "+paintCnt+", drawCnt = "+drawCnt+", fillCnt = "+fillCnt+", overFillCnt = "+overFillCnt+", drawBitmap = "+drawBitmap.getWidth()*drawBitmap.getHeight());
            filledRate = (float) fillCnt/paintCnt;
            overFilledRate = (float) overFillCnt/paintCnt;
            handler.post(() -> evaluateListener.onEvaluated(filledRate, overFilledRate));
        }).start();
    }

    public Bitmap getDrawBitmap(int baseCharacterWidth, int baseCharacterHeight){
        return Bitmap.createScaledBitmap(drawBitmap, baseCharacterWidth, baseCharacterHeight, true);
    }

    public void releaseAll(){

    }
}
