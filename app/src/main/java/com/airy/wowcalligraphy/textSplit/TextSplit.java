package com.airy.wowcalligraphy.textSplit;

/*
 * 这是文字切割的工具类，不应该在此使用
 * 放在这里仅是为了方便存储代码
 */

//import java.util.ArrayList;
//import org.opencv.core.Core;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;
//import org.opencv.core.Size;
//import org.opencv.highgui.HighGui;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//import net.sourceforge.pinyin4j.PinyinHelper;
//import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
//import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
//import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
//import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
//
//public class TextSplit {
//
//    public static void main(String[] args) {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        TextSplit ts = new TextSplit();
//        // ts.split(8, 0, "LanTing_KuaiRang", "快然自足不知老之将至", "img/LaoZhiJiangZhi.jpg", 12);
//        // ts.split(4, 0, "LanTing_TianLang", "天朗气清惠风和畅", "img/TianLangQiQing.jpg",50);
//        // ts.split(12, 0, "LanTing_Yangguan", "仰", "img/TianLangQiQing.jpg",50);
//        // ts.split(1, 1, "LanTing_Yangguan", "观宇宙之大俯察品类之盛", "img/GuanYvZhouZhiDa.jpg",12);
//    }
//
//    /**
//     * 将一张竖向、从右往左的书法按照文字分割
//     *
//     * @param start 从第start个字开始截取，这是从右往左的第一列、由上往下开始的
//     * @param startIdx 分割后的文字的起始index
//     * @param id    语句id，在app中使用该id选择图片
//     * @param text  语句
//     * @param imageUrl  待切割图像源
//     * @param splitPercent  文字与文字间隔的比例，这是为了防止如“旦”字被分割为“日”和“一”。玄学参数
//     */
//    void split(int start, int startIdx,String id ,String text, String imageUrl, int splitPercent){
//        Mat image = getBinaryImage(Imgcodecs.imread(imageUrl));
//        ArrayList<Mat> columnMats = splitColumns(image);
//        ArrayList<Mat> charcterMats = splitRows(columnMats, splitPercent);
//        System.out.println(charcterMats.size());
//        char[] textArr = text.toCharArray();
//        for (int i = 0; i < textArr.length; i++) {
//            Imgcodecs.imwrite("imgOut/"+id+"_"+(i+startIdx)+".jpg", charcterMats.get(start-1+i));
//        }
//    }
//
//    Mat getBinaryImage(Mat original){
//        int height = original.rows(), width = original.cols();
//        Mat grayImage = new Mat(height, width, CvType.CV_8SC1);
//        Mat binaryImage = new Mat(height, width, CvType.CV_8SC1);
//        Imgproc.cvtColor(original, grayImage,Imgproc.COLOR_RGB2GRAY);
//        Imgproc.blur(grayImage, grayImage, new Size(5,5));
//        Imgproc.threshold(grayImage, binaryImage, 125, 255, Imgproc.THRESH_BINARY);
//        // Imgproc.threshold(grayImage, binaryImage, 120, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
//        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(2,2));
//        Imgproc.morphologyEx(binaryImage,binaryImage,Imgproc.MORPH_OPEN,kernel);
//        Imgproc.morphologyEx(binaryImage,binaryImage,Imgproc.MORPH_CLOSE,kernel);
//        return binaryImage;
//    }
//
//    byte[][] mat2ByteArray(Mat image){
//        int row = image.rows(), column = image.cols();
//        byte[][] imageByte = new byte[row][column];
//        for (int i = 0; i < row; i++) {
//            image.get(i, 0, imageByte[i]);
//        }
//        return imageByte;
//    }
//
//    void printByteArrayInTerminal(byte[][] arr){
//        for (int i = 0; i < arr.length; i++) {
//            for (byte b : arr[i]) {
//                System.out.print(Math.abs(b));
//            }
//            System.out.println();
//        }
//    }
//
//    void printByteArrayInTerminal(byte[] arr){
//        for (byte b : arr) {
//            System.out.print(Math.abs(b));
//        }
//        System.out.println();
//    }
//
//    ArrayList<Mat> splitColumns(Mat image){
//        byte[][] imageByte = mat2ByteArray(image);
//        int row = imageByte.length, column = imageByte[0].length;
//        ArrayList<Mat> columnMats = new ArrayList<>();
//        int hasPaintIdx = 0, lastStart = 0;
//        boolean imageStart = true;
//        for (int i = 0; i < column; i++) {
//            boolean noPaintInColumn = true;
//            for (int j = 0; j < row; j++) {
//                if(imageByte[j][i]==0){
//                    noPaintInColumn = false;
//                    break;
//                }
//            }
//            if(!noPaintInColumn || i==column-1){
//                if(!imageStart && hasPaintIdx!=i-1){
//                    int mid = i==column-1?i:(hasPaintIdx+i)>>1;
//                    columnMats.add(image.submat(0, row-1, lastStart, mid));
//                    lastStart = mid+1;
//                }else imageStart = false;
//                hasPaintIdx = i;
//            }
//        }
//        return columnMats;
//    }
//
//    ArrayList<Mat> splitRows(ArrayList<Mat> columnMats, int splitPercent){
//        int textColumnSize = columnMats.size();
//        ArrayList<Mat> characterMats = new ArrayList<>();
//        for (int i = 1; i <= textColumnSize; i++) {
//            Mat curr = columnMats.get(textColumnSize-i);
//            byte[][] imageByte = mat2ByteArray(curr);
//            int row = imageByte.length, column = imageByte[0].length;
//            int hasPaintIdx = 0, lastStart = 0, lastTextHeight = 0;
//            boolean imageStart = true;
//            for (int j = 0; j < row; j++) {
//                boolean noPaintInRow = true;
//                for (int k = 0; k < column; k++) {
//                    if(imageByte[j][k]==0){
//                        noPaintInRow = false;
//                        break;
//                    }
//                }
//                // printByteArrayInTerminal(imageByte[j]);
//                if(!noPaintInRow || j==row-1){
//                    if(!imageStart && hasPaintIdx!=j-1){
//                        if(j!=row-1 && j-hasPaintIdx<lastTextHeight/splitPercent){
//                            // 防止部分汉字因其笔划不相连而导致被分离
//                            // 当前策略：若当前间隙小于上一个字的高度除以文字间隔比例，则认为这是字的一部分
//                            hasPaintIdx = j;
//                            continue;
//                        }
//                        int mid = (hasPaintIdx+j)>>1;
//                        characterMats.add(curr.submat(lastStart, mid, 0, column-1));
//                        lastTextHeight = mid - lastStart;
//                        lastStart = mid+1;
//                    }else imageStart = false;
//                    hasPaintIdx = j;
//                }
//            }
//        }
//        return characterMats;
//    }
//
//    String getPingying(String word){
//        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
//        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
//        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//        try {
//            return PinyinHelper.toHanYuPinyinString(word, format, "", true);
//        } catch (BadHanyuPinyinOutputFormatCombination e) {
//            return "";
//        }
//    }
//}
