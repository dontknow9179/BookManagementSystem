package database_connector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.sql.ResultSet;

import static database_connector.Connector.stmt;

public class Login {
    public static String user;
    public static String password;
    public static void showNewWindow() {
        // 创建一个新窗口
        OperationPanel newpanel = new OperationPanel();
        newpanel.CreateIt();
    }
    public static void showNewWindow_(){
        OperationPanelL newpanel = new OperationPanelL();
        newpanel.CreateIt();
    }
    public static void loginSystem(String user_id,String pass_word,Result result){
        /**判断用户的权限*/
        try {
            Class.forName(Connector.JDBC_DRIVER);//注册JDBC
            System.out.println("Connecting to database...");
            Connector.conn = DriverManager.getConnection(Connector.DB_URL, Connector.USER, Connector.PASS);//连接数据库
            stmt = Connector.conn.createStatement();//创建执行语句

            result.resultNum = 0;
            String sql = "select password from user_message where user_id = '"+user_id+"';";
            ResultSet resultSet = stmt.executeQuery(sql);
            Boolean flag = false;
            String p = "";
            String p_ = MD5.getMD5(pass_word);
            while (resultSet.next()){
                flag = true;
                p = resultSet.getString(1);
            }
            if(!flag){
                JOptionPane.showMessageDialog(null,"用户名错误","!",JOptionPane.WARNING_MESSAGE);
            }
            else if(p_.equals(p)==false){
                JOptionPane.showMessageDialog(null,"密码错误","!",JOptionPane.WARNING_MESSAGE);
            }
            else if(user_id.equals("user_0")){//注意！超级用户的用户名不能随便改
                result.resultNum = 1;//超级用户
            }
            else {
                result.resultNum = 2;//普通用户
            }

            resultSet.close();
            Connector.conn.close();
            stmt.close();
        }catch (Exception e){}
    }
    public void createIt(){
        JFrame jf = new JFrame("用户登录");
        jf.setSize(300,150);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // 第 1 个 JPanel, 使用默认的浮动布局
        JPanel panel01 = new JPanel();
        panel01.add(new JLabel("用户名"));
        JTextField jTextField1 = new JTextField(15);
        panel01.add(jTextField1);

        // 第 2 个 JPanel, 使用默认的浮动布局
        JPanel panel02 = new JPanel();
        panel02.add(new JLabel("密   码"));
        JTextField jTextField2 = new JPasswordField(15);
        panel02.add(jTextField2);

        // 第 3 个 JPanel, 使用浮动布局, 并且容器内组件居中显示
        JPanel panel03 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton login = new JButton("登录");
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 点击按钮, 判断是否显示新的一个窗口
                user = jTextField1.getText();
                password = jTextField2.getText();
                Result r = new Result();
                loginSystem(user,password,r);

                if(r.resultNum == 1) {
                    showNewWindow();
                    jf.dispose();
                }
                if(r.resultNum == 2){
                    showNewWindow_();
                    jf.dispose();
                }
            }
        });
        panel03.add(login);

        JButton logout = new JButton("退出");
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jf.dispose();
            }
        });
        panel03.add(logout);

        // 创建一个垂直盒子容器, 把上面 3 个 JPanel 串起来作为内容面板添加到窗口
        Box vBox = Box.createVerticalBox();
        vBox.add(panel01);
        vBox.add(panel02);
        vBox.add(panel03);

        jf.setContentPane(vBox);

        //jf.pack();
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
    }

}
