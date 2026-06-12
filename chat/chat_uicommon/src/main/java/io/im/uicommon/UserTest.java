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

    //头像地址
    private static final String[] AVATARS = {
            "https://img1.baidu.com/it/u=2448393511,2158991775&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500",
            "https://img2.baidu.com/it/u=503030755,4221479852&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500",
            "https://img2.baidu.com/it/u=238984594,2417541310&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500",
            "https://img2.baidu.com/it/u=449112416,2867994898&fm=253&fmt=auto&app=138&f=JPEG?w=502&h=500",
            "https://img1.baidu.com/it/u=1297111144,3353920078&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=510",
            "https://img2.baidu.com/it/u=4026604903,2442857774&fm=253&fmt=auto&app=138&f=JPEG?w=539&h=500",
            "https://img2.baidu.com/it/u=3985234655,539761375&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500",
            "https://img0.baidu.com/it/u=2314030843,967557479&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=800",
            "https://img2.baidu.com/it/u=3387573490,2974735163&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500",
            "https://img0.baidu.com/it/u=1450831860,1385355671&fm=253&fmt=auto&app=138&f=JPEG?w=812&h=800",
            "https://img0.baidu.com/it/u=3796344562,2479130368&fm=253&fmt=auto&app=138&f=JPEG?w=380&h=380",
            "https://img2.baidu.com/it/u=2631061309,535556176&fm=253&fmt=auto&app=138&f=JPEG?w=506&h=500",
            "https://img2.baidu.com/it/u=2413559666,3046760655&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500",
            "https://img1.baidu.com/it/u=1595036686,2514056083&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500",
            "https://img1.baidu.com/it/u=63940097,1466206767&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500",
            "https://img2.baidu.com/it/u=2762012932,383735120&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500",
            "https://img2.baidu.com/it/u=2359164495,1287697813&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500",
            "https://img1.baidu.com/it/u=2413283241,3744730483&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500",
            "https://img2.baidu.com/it/u=2902861314,1749265212&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500",
            "https://img2.baidu.com/it/u=2219732564,72261119&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500",
            "https://img1.baidu.com/it/u=1621616811,1488147250&fm=253&fmt=auto&app=138&f=JPEG?w=683&h=697",
            "https://img0.baidu.com/it/u=879204243,2409394269&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=800",
            "https://img0.baidu.com/it/u=1541558172,674379012&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500",
            "https://img2.baidu.com/it/u=2412866196,4209979760&fm=253&fmt=auto&app=138&f=JPEG?w=380&h=380",
            "https://img0.baidu.com/it/u=316879690,2861063914&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500",
            "https://img1.baidu.com/it/u=1347270311,1913305548&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500",
            "https://img2.baidu.com/it/u=2793076320,1589636997&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500",
            "https://img0.baidu.com/it/u=217268670,971933277&fm=253&fmt=auto&app=138&f=JPEG?w=400&h=400",
            "https://img0.baidu.com/it/u=2593116003,1982459658&fm=253&fmt=auto&app=138&f=JPEG?w=380&h=380",
            "https://img2.baidu.com/it/u=2147644476,1471332228&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=517",
            "https://img2.baidu.com/it/u=1488957107,1624644076&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500",
    };

    //常见姓氏
    private static final String[] LAST_NAMES = {
            "赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "褚", "卫", "蒋", "沈", "韩", "杨",
            "朱", "秦", "尤", "许", "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏", "陶", "姜",
            "戚", "谢", "邹", "喻", "柏", "水", "窦", "章", "云", "苏", "潘", "葛", "范", "彭", "郎", "鲁",
            "韦", "昌", "马", "苗", "凤", "花", "方", "俞", "任", "袁", "柳", "唐", "罗", "高", "林", "梁",
            "宋", "徐", "邓", "郭", "黄", "胡", "刘", "蔡"
    };

    //名字常用字
    private static final String[] NAME_WORDS = {
            "子", "晨", "辰", "文", "嘉", "然", "涵", "雨", "欣", "怡", "婷", "雪", "梦", "彤", "瑶", "佳",
            "依", "萌", "思", "妍", "琳", "雅", "婉", "阳", "宁", "诗", "悦", "可", "歆", "芸", "菲", "汐",
            "芷", "若", "兮", "茜", "璇", "蕊", "薇", "萱", "蓉", "珂", "晴", "露", "婧", "璐", "樱", "筱",
            "岚", "茉", "沁", "悠", "伊", "嫣", "柔", "柚", "鹿", "茶", "糖", "夏", "禾", "知", "念", "星",
            "月", "梨", "桃", "酥", "沐", "暖", "初", "晚", "七", "九", "糯"
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

    //创建随机用户
    public static UserInfo randomUser() {
        //随机头像
        String avatar = AVATARS[RANDOM.nextInt(AVATARS.length)];
        return new UserInfo(String.valueOf(Math.abs(ChatLibUtil.randomColor())), randomName(), avatar);
    }

    public static List<UserInfo> randomUserList() {
        List<UserInfo> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(randomUser());
        }
        return list;
    }


    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            JLog.i(randomName());
        }
    }
}
