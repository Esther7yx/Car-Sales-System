import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class generate_password {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 为不同角色创建密码
        System.out.println("admin (123456): " + encoder.encode("123456"));
        System.out.println("sales (123456): " + encoder.encode("123456"));
        System.out.println("warehouse (123456): " + encoder.encode("123456"));
        System.out.println("finance (123456): " + encoder.encode("123456"));
        System.out.println("manager (123456): " + encoder.encode("123456"));
    }
}
