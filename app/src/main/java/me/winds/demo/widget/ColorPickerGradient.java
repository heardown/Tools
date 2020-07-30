package me.winds.demo.widget;

import android.graphics.Color;

/**
 * Author:  winds
 * Email:   heardown@163.com
 * Date:    2019/9/9.
 * Desc:
 */
public class ColorPickerGradient {
    //设置的颜色
    public static final int[] PICK_COLOR_COLORS = {
            Color.parseColor("#FD5E45"), //红
            Color.parseColor("#FFD573"), //黄
            Color.parseColor("#55F0A2"), //绿
            Color.parseColor("#50D4FF"), //浅蓝
            Color.parseColor("#5FC6F6"), //深蓝
    };
    //每个颜色的位置
    public static final float[] PICK_COLOR_POSITIONS = {0.2f, 0.4f, 0.6f, 0.8f, 1f};

    private int[] colorArr = PICK_COLOR_COLORS;
    private float[] colorPosition = PICK_COLOR_POSITIONS;

    public ColorPickerGradient(int[] colorArr, float[] colorPos) {
        this.colorArr = colorArr;
        this.colorPosition = colorPos;
    }

    public ColorPickerGradient() {
    }

    /**
     * 获取某个百分比位置的颜色
     * @param radio 取值[0,1]
     * @return
     */
    public int getColor(float radio) {
        int startColor;
        int endColor;
        if (radio >= 1) {
            return colorArr[colorArr.length - 1];
        }
        for (int i = 0; i < colorPosition.length; i++) {
            if (radio <= colorPosition[i]) {
                if (i == 0) {
                    return colorArr[0];
                }
                startColor = colorArr[i - 1];
                endColor = colorArr[i];
                float areaRadio = getAreaRadio(radio,colorPosition[i-1],colorPosition[i]);
                return getColorFrom(startColor,endColor,areaRadio);
            }
        }
        return -1;
    }

    public float getAreaRadio(float radio, float startPosition, float endPosition) {
        return (radio - startPosition) / (endPosition - startPosition);
    }

    /**
     *  取两个颜色间的渐变区间 中的某一点的颜色
     * @param startColor
     * @param endColor
     * @param radio
     * @return
     */
    public int getColorFrom(int startColor, int endColor, float radio) {
        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);

        int red = (int) (redStart + ((redEnd - redStart) * radio + 0.5));
        int greed = (int) (greenStart + ((greenEnd - greenStart) * radio + 0.5));
        int blue = (int) (blueStart + ((blueEnd - blueStart) * radio + 0.5));
        return Color.argb(255, red, greed, blue);
    }

}
