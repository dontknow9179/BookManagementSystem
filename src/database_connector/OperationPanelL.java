package database_connector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.sql.ResultSet;

import static database_connector.Connector.stmt;

public class OperationPanelL {
    public void CreateIt(){
        JFrame jf = new JFrame("图书管理系统");
        jf.setSize(650, 400);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setLocationRelativeTo(null);

        // 创建选项卡面板
        final JTabbedPane tabbedPane = new JTabbedPane();

        // 创建选项卡（选项卡只包含 标题）
        tabbedPane.addTab("书籍检索", createSelectPanel());
        tabbedPane.addTab("书籍信息修改", createChangeBookPanel());
        tabbedPane.addTab("进货", createPurchasePanel());
        tabbedPane.addTab("付款/退货/到货",createChangeStatusPanel());
        tabbedPane.addTab("销售", createSoldPanel());
        tabbedPane.addTab("查账",createBillPanel());
        tabbedPane.addTab("查看或修改个人信息", createCheckUserPanel());


        // 设置默认选中的选项卡
        tabbedPane.setSelectedIndex(0);
        jf.setContentPane(tabbedPane);
        jf.setVisible(true);
    }

    /**
     * 创建一个面板，面板中心显示一个标签，用于表示某个选项卡需要显示的内容
     */
    //图书信息修改面板
    private static JComponent createChangeBookPanel(){
        JPanel panel00 = new JPanel(new BorderLayout());

        JPanel panel01 = new JPanel();
        panel01.add(new JLabel("输入要修改的图书的ISBN号："));
        JTextField jTextField1 = new JTextField(20);
        panel01.add(jTextField1);
        panel00.add(panel01,BorderLayout.NORTH);

        JPanel panel02 = new JPanel();
        panel02.add(new JLabel("修改成为："));
        JTextField jTextField2 = new JTextField(50);
        panel02.add(jTextField2);
        JButton btn = new JButton("确认");
        panel02.add(btn);
        // 创建单选按钮
        JRadioButton radioBtn01 = new JRadioButton("书名");
        JRadioButton radioBtn02 = new JRadioButton("作者");
        JRadioButton radioBtn03 = new JRadioButton("出版社");
        JRadioButton radioBtn04 = new JRadioButton("零售价");

        // 创建按钮组，把单选按钮添加到该组
        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(radioBtn01);
        btnGroup.add(radioBtn02);
        btnGroup.add(radioBtn03);
        btnGroup.add(radioBtn04);
        // 设置第一个单选按钮选中
        radioBtn01.setSelected(true);

        panel02.add(radioBtn01);
        panel02.add(radioBtn02);
        panel02.add(radioBtn03);
        panel02.add(radioBtn04);

        panel00.add(panel02,BorderLayout.CENTER);

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchInfo1 = jTextField1.getText();
                String searchInfo2 = jTextField2.getText();
                Result result1 = new Result();
                FunctionForSQL functionForSQL = new FunctionForSQL();
                functionForSQL.selectBook(searchInfo1,"ISBN",result1);
                if(result1.resultNum==0){
                    JOptionPane.showMessageDialog(null,"查无此书","!",JOptionPane.WARNING_MESSAGE);
                }
                else{
                    if(radioBtn01.isSelected()) {
                        functionForSQL.changeBook(searchInfo1,"book_name",searchInfo2 );
                        JOptionPane.showMessageDialog(null,"修改成功","!",JOptionPane.INFORMATION_MESSAGE);
                    }
                    if(radioBtn02.isSelected()){
                        functionForSQL.changeBook(searchInfo1,"author",searchInfo2);
                        JOptionPane.showMessageDialog(null,"修改成功","!",JOptionPane.INFORMATION_MESSAGE);
                    }
                    if(radioBtn03.isSelected()){
                        functionForSQL.changeBook(searchInfo1,"publisher",searchInfo2);
                        JOptionPane.showMessageDialog(null,"修改成功","!",JOptionPane.INFORMATION_MESSAGE);
                    }
                    if(radioBtn04.isSelected()){
                        functionForSQL.changeBook(searchInfo1,"price",searchInfo2);
                        JOptionPane.showMessageDialog(null,"修改成功","!",JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        return panel00;
    }
    //书籍销售面板
    private static JComponent createSoldPanel(){
        JPanel panel00 = new JPanel(new BorderLayout());

        JPanel panel01 = new JPanel();
        panel01.add(new JLabel("ISBN"));
        JTextField jt1 = new JTextField(13);
        panel01.add(jt1);

        JPanel panel02 = new JPanel();
        panel01.add(new JLabel("售出数目"));
        JTextField jt2 = new JTextField(13);
        panel01.add(jt2);

        JPanel panel03 = new JPanel();
        panel01.add(new JLabel("折扣"));
        JTextField jt3 = new JTextField(13);
        panel01.add(jt3);

        JPanel panel04 = new JPanel();
        JButton btn = new JButton("确认");
        panel04.add(btn);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String getISBN = jt1.getText();
                String getNum = jt2.getText();
                String getDiscount = jt3.getText();
                FunctionForSQL functionForSQL = new FunctionForSQL();
                Result r = new Result();
                /**每个框都要填*/
                if(getISBN.equals("")||getNum.equals("")||getDiscount.equals("")){
                    JOptionPane.showMessageDialog(null,"信息输入不足","!",JOptionPane.WARNING_MESSAGE);
                    return;
                }
                /**先检查ISBN*/
                functionForSQL.selectBook(getISBN,"ISBN",r);
                if(r.resultNum == 0){
                    JOptionPane.showMessageDialog(null,"查无此书","!",JOptionPane.WARNING_MESSAGE);
                    return;
                }
                /**检查销售量和折扣是否输入有误*/
                if(Float.parseFloat(getDiscount)<=0 || Integer.parseInt(getNum)<=0){
                    JOptionPane.showMessageDialog(null,"信息输入有误","!",JOptionPane.WARNING_MESSAGE);
                    return;
                }
                /**再检查库存*/
                functionForSQL.checkStock(getISBN,getNum,r);
                if(r.resultNum == 0){
                    JOptionPane.showMessageDialog(null,"库存不足","!",JOptionPane.WARNING_MESSAGE);
                    return;
                }

                else {
                    functionForSQL.sellBook(getISBN, getNum, getDiscount);
                    JOptionPane.showMessageDialog(null, "已售出", "!", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        Box vBox = Box.createVerticalBox();
        vBox.add(panel01);
        vBox.add(panel02);
        vBox.add(panel03);
        vBox.add(panel04);

        panel00.add(vBox,BorderLayout.NORTH);
        return panel00;
    }
    //书籍进货面板
    private static JComponent createPurchasePanel(){
        JPanel panel00 = new JPanel(new BorderLayout());

        JPanel panel01 = new JPanel();
        panel01.add(new JLabel("ISBN"));
        JTextField jTextField1 = new JTextField(19);
        panel01.add(jTextField1);

        panel00.add(panel01,BorderLayout.NORTH);

        Box vBox = Box.createVerticalBox();

        JPanel panel02 = new JPanel();
        panel02.add(new JLabel("书名"));
        JTextField jTextField2 = new JTextField(20);
        panel02.add(jTextField2);

        JPanel panel03 = new JPanel();
        panel03.add(new JLabel("作者"));
        JTextField jTextField3 = new JTextField(20);
        panel03.add(jTextField3);

        JPanel panel04 = new JPanel();
        panel04.add(new JLabel("出版社"));
        JTextField jTextField4 = new JTextField(19);
        panel04.add(jTextField4);

        JPanel panel05 = new JPanel();
        panel05.add(new JLabel("进货量"));
        JTextField jTextField5 = new JTextField(19);
        panel05.add(jTextField5);

        JPanel panel06 = new JPanel();
        panel06.add(new JLabel("进货价格"));
        JTextField jTextField6 = new JTextField(18);
        panel06.add(jTextField6);

        JPanel panel07 = new JPanel();
        panel07.add(new JLabel("零售价"));
        JTextField jTextField7 = new JTextField(19);
        panel07.add(jTextField7);

        vBox.add(panel02);
        vBox.add(panel03);
        vBox.add(panel04);
        vBox.add(panel05);
        vBox.add(panel06);
        vBox.add(panel07);

        panel00.add(vBox,BorderLayout.CENTER);

        JPanel panel08 = new JPanel();
        JButton btn1 = new JButton("确认（1）");
        JButton btn2 = new JButton("确认（2）");
        panel08.add(btn1);
        panel08.add(btn2);
        panel00.add(panel08,BorderLayout.SOUTH);

        btn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /**调用函数检查ISBN号，分两种情况更改面板*/
                /**不管哪一种都需要输入进货数目和进货价格，同时对数据库的二或者三个表做更改*/
                FunctionForSQL functionForSQL = new FunctionForSQL();
                Result result = new Result();
                String searchInfo = jTextField1.getText();
                functionForSQL.selectBook(searchInfo,"ISBN",result);
                if(result.resultNum == 0){
                    /**书架上没有这本书*/
                    JOptionPane.showMessageDialog(null,"所有文本框都需要填写","!",JOptionPane.INFORMATION_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog(null,"只需再输入进货量，进货价格","!",JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        btn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FunctionForSQL functionForSQL = new FunctionForSQL();
                Result result = new Result();
                String searchInfo = jTextField1.getText();
                functionForSQL.selectBook(searchInfo,"ISBN",result);
                if (result.resultNum==0){
                    String bookName = jTextField2.getText();
                    String author = jTextField3.getText();
                    String publisher = jTextField4.getText();
                    String num = jTextField5.getText();
                    String price_in = jTextField6.getText();
                    String price_out = jTextField7.getText();
                    /**考虑用户输入信息不足的情况*/
                    if(bookName.equals("") || author.equals("") || publisher.equals("") || num.equals("") || price_in.equals("") || price_out.equals("")){
                        JOptionPane.showMessageDialog(null,"输入信息不足","!",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    /**考虑用户输入信息有误的情况*/
                    if(Integer.parseInt(num)<=0 || Float.parseFloat(price_in)<=0 || Float.parseFloat(price_out)<=0){
                        JOptionPane.showMessageDialog(null,"输入信息有误","!",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    functionForSQL.purchaseBook(searchInfo,bookName,author,publisher,price_in,num,price_out);
                }
                else{
                    String num = jTextField5.getText();
                    String price = jTextField6.getText();
                    if(num.equals("") || price.equals("")){
                        JOptionPane.showMessageDialog(null,"输入信息不足","!",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if(Integer.parseInt(num)<=0 || Float.parseFloat(price)<=0){
                        JOptionPane.showMessageDialog(null,"输入信息有误","!",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    functionForSQL.purchaseBook_(searchInfo,price,num);
                }
                JOptionPane.showMessageDialog(null,"进货成功！","!",JOptionPane.INFORMATION_MESSAGE);
            }
        });


        return panel00;
    }
    //进货付款或退货或到货
    private static JComponent createChangeStatusPanel(){
        JPanel panel00 = new JPanel();
        FunctionForSQL functionForSQL = new FunctionForSQL();
        Result result = new Result();
        Result result_ = new Result();

        JPanel panel01 = new JPanel();
        panel01.add(new JLabel("查询进货单上的书籍"));
        JButton btn1 = new JButton("查询");
        panel01.add(btn1);

        JPanel panel02 = new JPanel();

        JPanel panel03 = new JPanel();
        panel03.add(new JLabel("输入要修改状态的订单号"));
        JTextField jTextField = new JTextField(10);
        panel03.add(jTextField);

        JPanel panel04 = new JPanel();
        JRadioButton radioBtn01 = new JRadioButton("付款");
        JRadioButton radioBtn02 = new JRadioButton("退货");
        JRadioButton radioBtn03 = new JRadioButton("到货");
        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(radioBtn01);
        btnGroup.add(radioBtn02);
        btnGroup.add(radioBtn03);
        radioBtn01.setSelected(true);
        panel04.add(radioBtn01);
        panel04.add(radioBtn02);
        panel04.add(radioBtn03);
        JButton btn2 = new JButton("确认修改");
        panel04.add(btn2);

        btn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel02.removeAll();
                functionForSQL.selectBook_(result);

                JTextArea jTextArea = new JTextArea(result.resultStr,6,12);
                jTextArea.setLineWrap(false);
                jTextArea.setEditable(false);
                JScrollPane jScrollPane = new JScrollPane(jTextArea);
                jScrollPane.setPreferredSize(new Dimension(600,200));
                panel02.add(jScrollPane);
                panel02.repaint();
            }
        });

        btn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String order = jTextField.getText();
                functionForSQL.judgeOrder(order,result_);
                if(result_.resultNum == 0){
                    JOptionPane.showMessageDialog(null,"此订单号不存在","!",JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if(radioBtn01.isSelected()){
                    functionForSQL.payForOrder(order,result_);
                    if(result_.resultNum == 0){
                        JOptionPane.showMessageDialog(null,"请选择未付款的订单","!",JOptionPane.WARNING_MESSAGE);
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "付款成功", "!", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                if(radioBtn02.isSelected()){
                    functionForSQL.returnBook(order,result_);
                    if(result_.resultNum == 1){
                        JOptionPane.showMessageDialog(null,"已退货","!",JOptionPane.INFORMATION_MESSAGE);
                    }
                    if(result_.resultNum == 0){
                        JOptionPane.showMessageDialog(null,"请选择未付款的订单","!",JOptionPane.WARNING_MESSAGE);
                    }
                }
                if(radioBtn03.isSelected()){
                    functionForSQL.receiveBook(order,result_);
                    if(result_.resultNum == 0){
                        JOptionPane.showMessageDialog(null,"请选择已付款的订单","!",JOptionPane.WARNING_MESSAGE);
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "已上架", "!", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        Box vBox = Box.createVerticalBox();
        vBox.add(panel01);
        vBox.add(panel02);
        vBox.add(panel03);
        vBox.add(panel04);

        panel00.add(vBox);
        return panel00;
    }
    //查看账单
    private static JComponent createBillPanel(){
        JPanel panel00 = new JPanel();

        JPanel panel10 = new JPanel();
        panel10.add(new JLabel("从"));
        JTextField jt10 = new JTextField(4);
        panel10.add(jt10);
        panel10.add(new JLabel("年"));

        JTextField jt11 = new JTextField(2);
        panel10.add(jt11);
        panel10.add(new JLabel("月"));

        JTextField jt12 = new JTextField(2);
        panel10.add(jt12);
        panel10.add(new JLabel("日"));

        JPanel panel11 = new JPanel();
        panel11.add(new JLabel("到"));
        JTextField jt20 = new JTextField(4);
        panel11.add(jt20);
        panel11.add(new JLabel("年"));

        JTextField jt21 = new JTextField(2);
        panel11.add(jt21);
        panel11.add(new JLabel("月"));

        JTextField jt22 = new JTextField(2);
        panel11.add(jt22);
        panel11.add(new JLabel("日"));

        JPanel panel12 = new JPanel();
        JCheckBox jCheckBox01 = new JCheckBox("收入");
        JCheckBox jCheckBox02 = new JCheckBox("支出");
        jCheckBox01.setSelected(true);
        JButton btn = new JButton("查看账单");
        panel12.add(jCheckBox01);
        panel12.add(jCheckBox02);
        panel12.add(btn);

        JPanel panel02 = new JPanel();//存放查询结果

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String y1 = jt10.getText();
                String m1 = jt11.getText();
                String d1 = jt12.getText();

                String y2 = jt20.getText();
                String m2 = jt21.getText();
                String d2 = jt22.getText();

                FunctionForSQL functionForSQL = new FunctionForSQL();
                Result r = new Result();
                r.resultStr = "";
                if(jCheckBox01.isSelected() && jCheckBox02.isSelected()){
                    functionForSQL.checkBill(1,y1,m1,d1,y2,m2,d2,r);
                }
                else if(jCheckBox01.isSelected()){
                    functionForSQL.checkBill(3,y1,m1,d1,y2,m2,d2,r);
                }
                else if(jCheckBox02.isSelected()){
                    functionForSQL.checkBill(2,y1,m1,d1,y2,m2,d2,r);
                }
                panel02.removeAll();
                JTextArea jTextArea = new JTextArea(r.resultStr,6,12);
                jTextArea.setLineWrap(false);
                jTextArea.setEditable(false);
                JScrollPane jScrollPane = new JScrollPane(jTextArea);
                jScrollPane.setPreferredSize(new Dimension(600,200));
                panel02.add(jScrollPane);
                panel02.repaint();

            }
        });

        Box vBox = Box.createVerticalBox();
        vBox.add(panel10);
        vBox.add(panel11);
        vBox.add(panel12);
        vBox.add(panel02);

        panel00.add(vBox);


        return panel00;
    }
    //书籍检索面板
    private static JComponent createSelectPanel(){
        JPanel panel00 = new JPanel(new BorderLayout());

        JPanel panel01 = new JPanel();
        panel01.add(new JLabel("检索"));
        JTextField jTextField = new JTextField(20);
        panel01.add(jTextField);
        JButton btn = new JButton("确认");
        panel01.add(btn);
        panel00.add(panel01,BorderLayout.NORTH);

        JPanel panel02 = new JPanel();
        // 创建单选按钮
        JRadioButton radioBtn01 = new JRadioButton("ISBN");
        JRadioButton radioBtn02 = new JRadioButton("书名");
        JRadioButton radioBtn03 = new JRadioButton("作者");
        JRadioButton radioBtn04 = new JRadioButton("出版社");

        // 创建按钮组，把单选按钮添加到该组
        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(radioBtn01);
        btnGroup.add(radioBtn02);
        btnGroup.add(radioBtn03);
        btnGroup.add(radioBtn04);
        // 设置第一个单选按钮选中
        radioBtn01.setSelected(true);

        panel02.add(radioBtn01);
        panel02.add(radioBtn02);
        panel02.add(radioBtn03);
        panel02.add(radioBtn04);

        panel00.add(panel02,BorderLayout.CENTER);
        JPanel panel03 = new JPanel();
        panel00.add(panel03,BorderLayout.SOUTH);

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                panel03.removeAll();
                String searchInfo = jTextField.getText();

                Result r = new Result();

                FunctionForSQL functionForSQL = new FunctionForSQL();
                if(radioBtn01.isSelected()){
                    functionForSQL.selectBook(searchInfo,"ISBN",r);
                }
                else if(radioBtn02.isSelected()){
                    functionForSQL.selectBook(searchInfo,"book_name",r);
                }
                else if(radioBtn03.isSelected()){
                    functionForSQL.selectBook(searchInfo,"author",r);
                }
                else {
                    functionForSQL.selectBook(searchInfo,"publisher",r);
                }
                if(r.resultNum==0){
                    JOptionPane.showMessageDialog(null,"查无此书","!",JOptionPane.WARNING_MESSAGE);
                    return;
                }
                JTextArea jTextArea = new JTextArea(r.resultStr,6,12);
                jTextArea.setLineWrap(false);
                jTextArea.setEditable(false);
                JScrollPane jScrollPane = new JScrollPane(jTextArea);
                jScrollPane.setPreferredSize(new Dimension(600,250));
                panel03.add(jScrollPane);
                panel03.repaint();

            }
        });
        return panel00;
    }
    //查看个人信息和更改密码面板
    private static JComponent createCheckUserPanel(){
        JPanel panel00 = new JPanel();
        String name = "";
        String wid = "";
        String gender = "";
        String age = "";
        String salary = "";
        try {
            Class.forName(Connector.JDBC_DRIVER);//注册JDBC
            System.out.println("Connecting to database...");
            Connector.conn = DriverManager.getConnection(Connector.DB_URL, Connector.USER, Connector.PASS);//连接数据库
            stmt = Connector.conn.createStatement();//创建执行语句

            String sql = "select * from user_message where user_id = '"+Login.user+"';";

            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()){
                name = resultSet.getString(2);
                wid = resultSet.getString(3);
                age = resultSet.getString(5);
                gender = resultSet.getString(6);
                salary = resultSet.getString(7);
            }

            resultSet.close();
            Connector.conn.close();
            stmt.close();
        }catch (Exception e){}

        JPanel panel01 = new JPanel();
        panel01.add(new JLabel("用户名："));
        panel01.add(new JLabel(Login.user));

        panel01.add(new JLabel("     真实姓名："));
        panel01.add(new JLabel(name));


        JPanel panel03 = new JPanel();
        panel03.add(new JLabel("工号："));
        panel03.add(new JLabel(wid));
        panel03.add(new JLabel("   性别："));
        panel03.add(new JLabel(gender));
        panel03.add(new JLabel("   年龄："));
        panel03.add(new JLabel(age));


        JPanel panel04 = new JPanel();
        panel04.add(new JLabel("工资："));
        panel04.add(new JLabel(salary));

        JPanel panel02 = new JPanel();
        panel02.add(new JLabel("修改个人信息"));
        // 创建单选按钮
        JRadioButton radioBtn01 = new JRadioButton("用户名");
        JRadioButton radioBtn02 = new JRadioButton("真实姓名");
        JRadioButton radioBtn03 = new JRadioButton("年龄");
        JRadioButton radioBtn04 = new JRadioButton("工资");

        // 创建按钮组，把单选按钮添加到该组
        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(radioBtn01);
        btnGroup.add(radioBtn02);
        btnGroup.add(radioBtn03);
        btnGroup.add(radioBtn04);
        // 设置第一个单选按钮选中
        radioBtn01.setSelected(true);

        panel02.add(radioBtn01);
        panel02.add(radioBtn02);
        panel02.add(radioBtn03);
        panel02.add(radioBtn04);

        JPanel panel05 = new JPanel();
        panel05.add(new JLabel("修改成为："));
        JTextField jt1 = new JTextField(15);
        panel05.add(jt1);
        JButton btn1 = new JButton("确认修改");
        panel05.add(btn1);
        btn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Result result1 = new Result();
                FunctionForSQL functionForSQL = new FunctionForSQL();
                String info = jt1.getText();
                try {
                    Class.forName(Connector.JDBC_DRIVER);//注册JDBC
                    System.out.println("Connecting to database...");
                    Connector.conn = DriverManager.getConnection(Connector.DB_URL, Connector.USER, Connector.PASS);//连接数据库
                    stmt = Connector.conn.createStatement();//创建执行语句
                    if(radioBtn01.isSelected()){
                        String sql1 = "update user_message set user_id = '"+info+"' where user_id = '"+Login.user+"';";
                        stmt.execute(sql1);
                        JOptionPane.showMessageDialog(null,"修改成功，请重新登录");
                    }
                    if(radioBtn02.isSelected()){
                        String sql2 = "update user_message set name = '"+info+"' where user_id = '"+Login.user+"';";
                        stmt.execute(sql2);
                        JOptionPane.showMessageDialog(null,"修改成功，请重新登录");
                    }
                    if(radioBtn03.isSelected()){
                        String sql3 = "update user_message set age = '"+info+"' where user_id = '"+Login.user+"';";
                        stmt.execute(sql3);
                        JOptionPane.showMessageDialog(null,"修改成功，请重新登录");
                    }
                    if(radioBtn04.isSelected()){
                        String sql4 = "update user_message set salary = '"+info+"' where user_id = '"+Login.user+"';";
                        stmt.execute(sql4);
                        JOptionPane.showMessageDialog(null,"修改成功，请重新登录");
                    }

                    Connector.conn.close();
                    stmt.close();
                }catch (Exception e1){}

            }
        });

        JPanel panel06 = new JPanel();
        panel06.add(new JLabel("修改密码"));

        JPanel panel07 = new JPanel();
        panel07.add(new JLabel("输入新密码："));
        JTextField jt2 = new JPasswordField(10);
        panel07.add(jt2);
        panel07.add(new JLabel("    再次输入密码："));
        JTextField jt3 = new JPasswordField(10);
        panel07.add(jt3);

        JPanel panel08 = new JPanel();
        JButton btn2 = new JButton("修改密码");
        panel08.add(btn2);
        btn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pass1 = jt2.getText();
                String pass2 = jt3.getText();
                /**先检查两次输入的新密码是否相同*/
                if(!pass1.equals(pass2)){
                    JOptionPane.showMessageDialog(null,"两次输入的密码不一致，请重新输入");
                    return;
                }
                /**修改密码*/
                try {
                    Class.forName(Connector.JDBC_DRIVER);//注册JDBC
                    System.out.println("Connecting to database...");
                    Connector.conn = DriverManager.getConnection(Connector.DB_URL, Connector.USER, Connector.PASS);//连接数据库
                    stmt = Connector.conn.createStatement();//创建执行语句
                    String newPassword = MD5.getMD5(pass1);
                    String sql = "update user_message set password = '"+newPassword+"' where user_id ='"+Login.user+"';";
                    stmt.execute(sql);
                    System.out.println(newPassword);
                    JOptionPane.showMessageDialog(null,"密码修改成功，请重新登录");

                    Connector.conn.close();
                    stmt.close();
                }catch (Exception e1){}
            }
        });

        Box vBox = Box.createVerticalBox();
        vBox.add(panel01);
        vBox.add(panel03);
        vBox.add(panel04);
        vBox.add(panel02);
        vBox.add(panel05);
        vBox.add(panel06);
        vBox.add(panel07);
        vBox.add(panel08);

        panel00.add(vBox);
        return panel00;
    }

}
