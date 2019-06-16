package zx.ias;

import zx.ias.bean.People;
import zx.ias.ui.DataManager;
import zx.ias.ui.Login;
import zx.ias.util.DebugLog;
import zx.ias.util.IO;
import zx.ias.util.UI;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

/**
 * MainEntry
 * 运行环境：Windows 10(1903),
 * java version "12" 2019-03-19
 * Java(TM) SE Runtime Environment (build 12+33)
 * Java HotSpot(TM) 64-Bit Server VM (build 12+33, mixed mode, sharing)
 */

public class Main {

    /**
     * 开关：用来生成随机数据以调试
     */
    private static final boolean GENERATE_RANDOM_MODE = Global.RANDOM_DATA_GENERATE_MODE;

    public static void main(String[] args) {
        if (GENERATE_RANDOM_MODE) {
            ArrayList<People> arrayList = new ArrayList<>();
            Random r = new Random();
            for (byte b : new byte[1000]) {
                People people = new People();

                //UTF-8 汉字范围
                people.setName(randomChar(3, new int[]{0x4e00, 0x9fa5}));

                //随机性别
                people.setSex(People.SexType.values()[r.nextInt(2)]);

                //1970-约2010
                people.setBirthday(LocalDate.ofEpochDay(r.nextInt(365 * 40)));

                //大小写字母
                people.setAddress(randomChar(20, new int[][]{{0x41, 0x5A}, {0x61, 0x7A}}[r.nextInt(2)]));
                DebugLog.i("Create random people info:" + people);
                arrayList.add(people);
            }
            People[] peopleArray = new People[arrayList.size()];
            DebugLog.i("Random people info DATA FILE " + IO.obj2File(DataManager.CFG_FILENAME, arrayList.toArray(peopleArray)));
            JOptionPane.showMessageDialog(null, "随机数据已生成", DataManager.CFG_FILENAME, JOptionPane.PLAIN_MESSAGE);
        } else {
            /*主要*/
            UI.configSystemDefaultUIStyle();
            new Login();
        }
    }

    /**
     * 随机字符
     *
     * @param count 生成个数
     * @param range 长度2，范围{@code min}到{@code max}
     * @return 生成的字符
     */
    private static String randomChar(int count, int[] range) {
        int min = range[0], max = range[1];
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            int index = new Random().nextInt(max) % (max - min + 1) + min;
            builder.append((char) index);
        }
        return builder.toString();
    }
}
