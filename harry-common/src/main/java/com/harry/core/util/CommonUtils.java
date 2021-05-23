package com.harry.core.util;

public class CommonUtils {

    public static String createKey() {
        String randomcode = "";
        // 用字符数组的方式随机
        String model = "b0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        char[] m = model.toCharArray();
        for (int j = 0; j < 6; j++) {
            char c = m[(int) (Math.random() * 36)];
            // 保证六位随机数之间没有重复的
            if (randomcode.contains(String.valueOf(c))) {
                j--;
                continue;
            }
            randomcode = randomcode + c;
        }

        String s = String.valueOf(System.currentTimeMillis());
        //TODO 查询数据库校验重复
//        return randomcode + s.substring(7, 13);
        return randomcode.substring(0, 3) + s.substring(7, 10)
                +randomcode.substring(3, 6)+ s.substring(10, 13);
    }

//    public static User getLoginUser() {
//        HttpServletRequest request =
//                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        HttpSession session = request.getSession();
//        return (User) session.getAttribute("U");
//    }

    public static void main(String[] args) {
        System.out.println(createKey());
    }
}

