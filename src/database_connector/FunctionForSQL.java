package database_connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.IllegalFormatCodePointException;

import static database_connector.Connector.stmt;

public class FunctionForSQL {
    //创建新用户
    public void createUser(String user_id,String name,String wid,String password,String age,String gender,String salary,Result result){
        try {
            Class.forName(Connector.JDBC_DRIVER);//注册JDBC
            System.out.println("Connecting to database...");
            Connector.conn = DriverManager.getConnection(Connector.DB_URL, Connector.USER, Connector.PASS);//连接数据库
            stmt = Connector.conn.createStatement();//创建执行语句
            /**用户名查重*/
            String sql0 = "select user_id from user_message;";
            ResultSet resultSet = stmt.executeQuery(sql0);
            Boolean flag = true;
            while (resultSet.next()){
                if(resultSet.getString(1).equals(user_id))
                    flag = false;
            }
            if(flag == false){
                result.resultNum = 0;
            }
            else {
                /**先对密码加密*/
                String p = MD5.getMD5(password);
                System.out.println(p);
                String sql = "insert into user_message values ('" + user_id + "','" + name +
                        "'," + wid + ",'" + p + "'," + age + ",'" + gender + "'," + salary + ");";
                stmt.execute(sql);
                result.resultNum = 1;
            }
            resultSet.close();
            Connector.conn.close();
            stmt.close();
        }catch (Exception e){}
    }
    //查看所有用户
    public void selectAllUser(Result result){
        try {
            Class.forName(Connector.JDBC_DRIVER);//注册JDBC
            System.out.println("Connecting to database...");
            Connector.conn = DriverManager.getConnection(Connector.DB_URL, Connector.USER, Connector.PASS);//连接数据库
            stmt = Connector.conn.createStatement();//创建执行语句

            result.resultStr = "";
            String sql = "select * from user_message;";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()){
                result.resultStr += "用户名："+resultSet.getString(1) +
                        "   真实姓名："+resultSet.getString(2) +
                        "   工号：" +resultSet.getString(3) +

                        "   年龄：" +resultSet.getString(5) +
                        "   性别：" +resultSet.getString(6) +
                        "   工资：" +resultSet.getString(7) +"\n\n";
            }
            resultSet.close();

            Connector.conn.close();
            stmt.close();
        }catch (Exception e){}
    }
    //书籍检索
    public void selectBook(String searchInfo,String choice,Result result){
        String output = "";
        int i = 0;
        try {
            Class.forName(Connector.JDBC_DRIVER);//注册JDBC
            System.out.println("Connecting to database...");
            Connector.conn = DriverManager.getConnection(Connector.DB_URL,Connector.USER,Connector.PASS);//连接数据库
            stmt = Connector.conn.createStatement();//创建执行语句

            String sql = "select * from book_store where " + choice + " = '" + searchInfo + "'";
            String selectResult;
            ResultSet resultSet = stmt.executeQuery(sql);

            while(resultSet.next()){
                selectResult = " ISBN："+resultSet.getString(1)+"  "+
                        "书名："+resultSet.getString(2)+"  "+
                        "出版社："+resultSet.getString(3)+"  "+
                        "作者："+resultSet.getString(4)+"  "+
                        "库存："+resultSet.getString(5)+"  "+
                        "售价："+resultSet.getString(6)+"  ";
                i++;
                System.out.println(selectResult);
                output += selectResult + "\n\n";
            }

            Connector.conn.close();
            stmt.close();
            resultSet.close();
        }catch(Exception e){}

        result.resultNum = i;
        result.resultStr = output;

    }
    //查看进货表
    public void selectBook_(Result result){
        String output = "";
        int i = 0;
        try {
            Class.forName(Connector.JDBC_DRIVER);//注册JDBC
            System.out.println("Connecting to database...");
            Connector.conn = DriverManager.getConnection(Connector.DB_URL, Connector.USER, Connector.PASS);//连接数据库
            stmt = Connector.conn.createStatement();//创建执行语句

            String sql = "select * from book_purchase;" ;
            ResultSet resultSet = stmt.executeQuery(sql);
            String selectResult;

            while(resultSet.next()){
                selectResult = " 订单号："+resultSet.getString(1)+"  "+
                        "ISBN："+resultSet.getString(2)+"  "+
                        "数量："+resultSet.getString(3)+"  "+
                        "进货价格："+resultSet.getString(4)+"  "+
                        "状态："+resultSet.getString(5)+"  ";
                i++;

                output += selectResult + "\n\n";
            }
            Connector.conn.close();
            stmt.close();
            resultSet.close();
        }catch (Exception e){}
        result.resultNum = i;
        result.resultStr = output;
    }
    //检查书的库存量是否充足
    public void checkStock(String getISBN,String getNum,Result result){
        try {
            Class.forName(Connector.JDBC_DRIVER);//注册JDBC
            System.out.println("Connecting to database...");
            Connector.conn = DriverManager.getConnection(Connector.DB_URL, Connector.USER, Connector.PASS);//连接数据库
            stmt = Connector.conn.createStatement();//创建执行语句

            String sql = "select stock from book_store where ISBN =" + getISBN;
            ResultSet resultSet = stmt.executeQuery(sql);
            Boolean flag = false;
            while (resultSet.next()){
                flag = Integer.parseInt(resultSet.getString(1)) >= Integer.parseInt(getNum);
            }
            if(flag)
                result.resultNum = 1;
            else
                result.resultNum = 0;

            resultSet.close();
            Connector.conn.close();
            stmt.close();
        }catch (Exception e){}
    }
    //正常情况下卖书
    public void sellBook(String getISBN,String getNum,String getDiscount){
        try {
            Class.forName(Connector.JDBC_DRIVER);//注册JDBC
            System.out.println("Connecting to database...");
            Connector.conn = DriverManager.getConnection(Connector.DB_URL, Connector.USER, Connector.PASS);//连接数据库
            stmt = Connector.conn.createStatement();//创建执行语句

            /**将售出的记录更新*/
            String sql3 = "insert into book_sold values (  null ,'" + getISBN + "'" + "," + getNum + "," + getDiscount + ");";
            stmt.execute(sql3);

            /**将账单更新*/
            String money = "";
            float money_f = 0;
            String sql4_ = "select price,num_out,discount from book_sold natural join book_store where `order`= (select max(`order`) from book_sold)";
            ResultSet resultSet = stmt.executeQuery(sql4_);
            while (resultSet.next()){
                money_f = Float.parseFloat(resultSet.getString(1)) *
                        Float.parseFloat(resultSet.getString(2)) *
                        Float.parseFloat(resultSet.getString(3));
            }
            money = String.format("%.2f",money_f).toString();
            String sql4 = "insert into trade_record values ( null ,'" + getISBN + "'" + "," + money + ",localtimestamp)";
            stmt.execute(sql4);

            /**将库存量更新*/
            String sql1 = "update book_store set stock = stock - " + getNum + " where ISBN = " + getISBN;
            stmt.execute(sql1);
            /**如果卖完最后一本就要删掉*/
            String sql2 = "delete from book_store where stock = 0 and ISBN = " + getISBN;
            stmt.execute(sql2);

            Connector.conn.close();
            stmt.close();
            resultSet.close();
        }catch (Exception e){}

    }
    //修改图书信息
    public void changeBook(String getISBN,String choice,String changeInfo){
        try {
            Class.forName(Connector.JDBC_DRIVER);//注册JDBC
            System.out.println("Connecting to database...");
            Connector.conn = DriverManager.getConnection(Connector.DB_URL, Connector.USER, Connector.PASS);//连接数据库
            stmt = Connector.conn.createStatement();//创建执行语句
            /**根据选项将选中的书本更新*/
            String sql = "update book_store set " + choice + "= '" +changeInfo + "' where ISBN = " + getISBN;
            stmt.execute(sql);
            Connector.conn.close();
            stmt.close();
        }catch (Exception e){}
    }
    //订单号输入判断
    public void judgeOrder(String order,Result result){
        try {
            Class.forName(Connector.JDBC_DRIVER);//注册JDBC
            System.out.println("Connecting to database...");
            Connector.conn = DriverManager.getConnection(Connector.DB_URL, Connector.USER, Connector.PASS);//连接数据库
            stmt = Connector.conn.createStatement();//创建执行语句
            /**检查订单号是否有误*/
            String sql = "select * from book_purchase where `order` = " + order;
            ResultSet resultSet = stmt.executeQuery(sql);
            if (!resultSet.next()){
                result.resultNum = 0;
            }
            else result.resultNum = 1;

            resultSet.close();
            Connector.conn.close();
            stmt.close();
        }catch (Exception e){}
    }
    //订单付款
    public void payForOrder(String order,Result result){
        try {
            Class.forName(Connector.JDBC_DRIVER);//注册JDBC
            System.out.println("Connecting to database...");
            Connector.conn = DriverManager.getConnection(Connector.DB_URL, Connector.USER, Connector.PASS);//连接数据库
            stmt = Connector.conn.createStatement();//创建执行语句

            /**检查要修改状态的订单是否未付款*/
            String sql0 = "select status from book_purchase where `order`=" +order;
            ResultSet resultSet = stmt.executeQuery(sql0);
            while (resultSet.next()){
                result.resultStr = resultSet.getString(1);
            }
            resultSet.close();
            if(result.resultStr.equals("unpaid")) {
                result.resultNum = 1;
                /**将进货清单中选中的订单状态设置为已付款*/
                String sql1 = "update book_purchase set status = 'paid' where `order`=" + order;
                stmt.execute(sql1);
                /**修改收支表增加支出记录*/
                String sql2 = "insert into trade_record values (null ,(select ISBN from book_purchase where book_purchase.`order`="
                        + order + "),-(select num * price_in FROM book_purchase WHERE `order` = " + order + "),localtimestamp);";
                stmt.execute(sql2);
            }
            else {
                result.resultNum = 0;
            }
            Connector.conn.close();
            stmt.close();
        }catch (Exception e){}
    }
    //退货
    public void returnBook(String order,Result result){
        try {
            Class.forName(Connector.JDBC_DRIVER);//注册JDBC
            System.out.println("Connecting to database...");
            Connector.conn = DriverManager.getConnection(Connector.DB_URL, Connector.USER, Connector.PASS);//连接数据库
            stmt = Connector.conn.createStatement();//创建执行语句

            /**检查要修改状态的订单是否已付款*/
            String sql1 = "select status from book_purchase where `order`=" +order;
            ResultSet resultSet = stmt.executeQuery(sql1);
            while (resultSet.next()){
                result.resultStr = resultSet.getString(1);
            }
            System.out.println(result.resultStr);
            if(result.resultStr .equals("unpaid")) {
                //System.out.println("H");
                result.resultNum = 1;
                //System.out.println("H");
                /**将进货清单中选中的未付款订单状态设置为已退货*/
                String sql2 = "update book_purchase set status = 'returned' where `order`=" + order;
                stmt.execute(sql2);
                //System.out.println("2h");
                /**退货后将书架上对应的库存为0的书删掉*/
                /**这个问题超超超麻烦，要考虑到可能进了两次同样的书，但只退掉其中一单，这时候不能删，要等到所有都退了才删*/
                /**理解为每次修改后都检查一次*/
                boolean flag = false;/**flag==true时不删*/
                String sql3 = "select ISBN from book_purchase where `order`=" +order;
                resultSet = stmt.executeQuery(sql3);
                //System.out.println("3H");
                String ISBN = "";
                while (resultSet.next()){
                    ISBN = resultSet.getString(1);
                }
                System.out.println(ISBN);

                /**order不要乱输就一定会有且仅有一个ISBN*/
                String sql4 = "select status from book_purchase where ISBN = " + ISBN;
                resultSet = stmt.executeQuery(sql4);
                while (resultSet.next()){
                    if(resultSet.getString(1).equals("returned") == false)
                        flag = true;/**只要有一个不是returned就不能删掉这本书*/
                }
                if (flag == false){
                    String sql5 = "delete from book_store where stock = 0 and ISBN = " + ISBN;
                    stmt.execute(sql5);
                }

            }
            /**若已付款等其他情况则无法进行操作*/
            else {
                result.resultNum = 0;
            }
            resultSet.close();
            Connector.conn.close();
            stmt.close();
        }catch (Exception e){}
    }
    //收货
    public void receiveBook(String order,Result result){
        try {
            Class.forName(Connector.JDBC_DRIVER);//注册JDBC
            System.out.println("Connecting to database...");
            Connector.conn = DriverManager.getConnection(Connector.DB_URL, Connector.USER, Connector.PASS);//连接数据库
            stmt = Connector.conn.createStatement();//创建执行语句

            /**检查要修改状态的订单是否已付款*/
            String sql0 = "select status from book_purchase where `order`=" +order;
            ResultSet resultSet = stmt.executeQuery(sql0);
            String r = "";
            while (resultSet.next()){
                r = resultSet.getString(1);
            }
            if(r.equals("paid")==true) {
                result.resultNum = 1;
                String sql1 = "update book_purchase set status = 'received' where `order` =" + order;
                stmt.execute(sql1);
                String sql2 = "update book_store set stock = stock + (select num from book_purchase where `order` = " + order
                        + ") where ISBN = (select ISBN from book_purchase where `order` = " + order + ");";
                stmt.execute(sql2);
            }
            else {
                result.resultNum = 0;
            }
            Connector.conn.close();
            stmt.close();
        }catch (Exception e){}
    }
    //查账
    public void checkBill(int choice,String y1,String m1,String d1,String y2,String m2,String d2,Result result){
        try {
            Class.forName(Connector.JDBC_DRIVER);//注册JDBC
            System.out.println("Connecting to database...");
            Connector.conn = DriverManager.getConnection(Connector.DB_URL, Connector.USER, Connector.PASS);//连接数据库
            stmt = Connector.conn.createStatement();//创建执行语句
            /**收支都查*/
            if(choice == 1){
                String sql1 = "select * from trade_record where deal_time between '"+y1+"-"+m1+"-"+d1+
                        "' and '"+y2+"-"+m2+"-"+d2+"';";
                ResultSet resultSet1 = stmt.executeQuery(sql1);
                while (resultSet1.next()){
                    result.resultStr += " order: " + resultSet1.getString(1) +
                            "   ISBN: " + resultSet1.getString(2) +
                            "   账目: " + resultSet1.getString(3) +
                            "   时间: " + resultSet1.getString(4) + "\n\n";
                }
                resultSet1.close();
                String sql1_ = "select sum(deal_sum) from trade_record where deal_time between '"+y1+"-"+m1+"-"+d1+
                        "' and '"+y2+"-"+m2+"-"+d2+"';";
                ResultSet resultSet1_ = stmt.executeQuery(sql1_);
                while (resultSet1_.next()) {
                    result.resultStr += "总收支：" + resultSet1_.getString(1);
                }
                resultSet1_.close();
            }

            /**只查支出*/
            if(choice == 2){
                String sql2 = "select * from trade_record where deal_time between '"+y1+"-"+m1+"-"+d1+
                        "' and '"+y2+"-"+m2+"-"+d2+"' and deal_sum < 0;";
                ResultSet resultSet2 = stmt.executeQuery(sql2);
                while (resultSet2.next()){
                    result.resultStr += " order: " + resultSet2.getString(1) +
                            "   ISBN: " + resultSet2.getString(2) +
                            "   账目: " + resultSet2.getString(3) +
                            "   时间: " + resultSet2.getString(4) + "\n\n";
                }
                resultSet2.close();
                String sql2_ = "select sum(deal_sum) from trade_record where deal_time between '"+y1+"-"+m1+"-"+d1+
                        "' and '"+y2+"-"+m2+"-"+d2+"' and deal_sum < 0;";
                ResultSet resultSet2_ = stmt.executeQuery(sql2_);
                while (resultSet2_.next()) {
                    result.resultStr += "总支出：" + resultSet2_.getString(1);
                }
                resultSet2_.close();
            }

            /**只查收入*/
            if(choice == 3){
                String sql3 = "select * from trade_record where deal_time between '"+y1+"-"+m1+"-"+d1+
                        "' and '"+y2+"-"+m2+"-"+d2+"' and deal_sum > 0;";
                ResultSet resultSet3 = stmt.executeQuery(sql3);
                while (resultSet3.next()){
                    result.resultStr += " order: " + resultSet3.getString(1) +
                            "   ISBN: " + resultSet3.getString(2) +
                            "   账目: " + resultSet3.getString(3) +
                            "   时间: " + resultSet3.getString(4) + "\n\n";
                }
                resultSet3.close();
                String sql3_ = "select sum(deal_sum) from trade_record where deal_time between '"+y1+"-"+m1+"-"+d1+
                        "' and '"+y2+"-"+m2+"-"+d2+"' and deal_sum > 0;";
                ResultSet resultSet3_ = stmt.executeQuery(sql3_);
                while (resultSet3_.next()){
                    result.resultStr += "总收入：" + resultSet3_.getString(1);
                }
                resultSet3_.close();
            }

            Connector.conn.close();
            stmt.close();
        }catch (Exception e){}
    }
    //买入架上存在的图书
    public void purchaseBook_(String ISBN,String price_in,String num){
        try {
            Class.forName(Connector.JDBC_DRIVER);//注册JDBC
            System.out.println("Connecting to database...");
            Connector.conn = DriverManager.getConnection(Connector.DB_URL, Connector.USER, Connector.PASS);//连接数据库
            stmt = Connector.conn.createStatement();//创建执行语句
            /**将购入的书放入进货清单,状态设置为未付款*/
            String sql1 = "insert into book_purchase values (null,'" + ISBN + "'," + num + "," + price_in + ",'unpaid')" ;
            stmt.execute(sql1);

            Connector.conn.close();
            stmt.close();
        }catch (Exception e){}
    }
    //买入架上没有的书
    public void purchaseBook(String ISBN,String bookName,String author,String publisher,String price_in,String num,String price_out){
        try {
            Class.forName(Connector.JDBC_DRIVER);//注册JDBC
            System.out.println("Connecting to database...");
            Connector.conn = DriverManager.getConnection(Connector.DB_URL, Connector.USER, Connector.PASS);//连接数据库
            stmt = Connector.conn.createStatement();//创建执行语句
            /**将购入的书放入进货清单,状态设置为未付款*/
            String sql1 = "insert into book_purchase values (null,'"  + ISBN + "'," + num + "," + price_in + ",'unpaid')" ;
            stmt.execute(sql1);
            /**将购入的书的信息先预存入书架，库存量为0*/
            String sql2 = "insert into book_store values ('" + ISBN + "','" + bookName + "','" +
                    publisher + "','" + author + "',0," + price_out + ");" ;
            stmt.execute(sql2);

            Connector.conn.close();
            stmt.close();
        }catch (Exception e){}
    }
}
