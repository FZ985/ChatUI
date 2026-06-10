package io.im.uicommon;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.im.core.model.UserInfo;
import io.im.core.utils.ChatLibUtil;
import io.im.core.utils.JLog;

/**
 * by DAD FZ
 * 2026/6/10
 * desc：
 **/
public class UserTest {

    private static final Random RANDOM = new Random();

    /**
     * 常见姓氏
     */
    private static final String[] LAST_NAMES = {
            "赵", "钱", "孙", "李", "周", "吴", "郑", "王",
            "冯", "陈", "褚", "卫", "蒋", "沈", "韩", "杨",
            "朱", "秦", "尤", "许", "何", "吕", "施", "张",
            "孔", "曹", "严", "华", "金", "魏", "陶", "姜",
            "戚", "谢", "邹", "喻", "柏", "水", "窦", "章",
            "云", "苏", "潘", "葛", "范", "彭", "郎", "鲁",
            "韦", "昌", "马", "苗", "凤", "花", "方", "俞",
            "任", "袁", "柳", "唐", "罗", "高", "林", "梁",
            "宋", "徐", "邓", "郭", "黄", "胡", "刘", "蔡"
    };

    /**
     * 名字常用字
     */
    private static final String[] NAME_WORDS = {
            "子", "轩", "宇", "浩", "晨", "辰", "博", "文",
            "嘉", "俊", "睿", "哲", "泽", "铭", "然", "涵",
            "雨", "欣", "怡", "婷", "雪", "梦", "彤", "瑶",
            "佳", "依", "萌", "思", "妍", "琳", "雅", "婉",
            "昊", "航", "阳", "宁", "毅", "峰", "凯", "杰"
    };

    private UserTest() {
    }

    /**
     * 生成随机中文姓名
     */
    public static String randomName() {
        String lastName = LAST_NAMES[RANDOM.nextInt(LAST_NAMES.length)];

        // 名字随机 1~2 个字
        int nameLength = RANDOM.nextBoolean() ? 1 : 2;

        StringBuilder firstName = new StringBuilder();
        for (int i = 0; i < nameLength; i++) {
            firstName.append(NAME_WORDS[RANDOM.nextInt(NAME_WORDS.length)]);
        }

        return lastName + firstName;
    }


    public static List<UserInfo> randomUserList() {
        List<UserInfo> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(randomUser());
        }
        return list;
    }


    public static UserInfo randomUser() {
        return new UserInfo(String.valueOf(ChatLibUtil.randomColor()), randomName(), "");
    }

    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            JLog.i(randomName());
        }
    }
}
