package zx.ias.ui;

import zx.ias.bean.AdminInfo;
import zx.ias.util.DebugLog;
import zx.ias.util.IO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Objects;

public class Login extends JFrame implements ActionListener {

    /**
     * 登录配置文件
     */
    static final String CFG_FILENAME = "login.cfg";

    /**
     * 文本域宽度
     */
    private static final int TEXT_FIELD_COL = 30;

    private JTextField usrNameField = new JTextField(TEXT_FIELD_COL);
    private JPasswordField pwdField = new JPasswordField(TEXT_FIELD_COL);
    private JButton enterBtn = new JButton("进入");

    public Login() {

        initInternalComponent();

        /* 窗体调整 */

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //网状布局，行4（欢迎，用户名，密码，进入）列1
        setLayout(new GridLayout(4, 1));
        setResizable(false);
        setTitle("登录");

        //自适应窗口大小
        pack();
        DebugLog.i("Window Size: " + String.format("%d*%d", getSize().width, getSize().height));

        //无位置依赖表示居中，注意保证窗口已经定型
        setLocationRelativeTo(null);

        if (!checkLoginConfig())
            JOptionPane.showMessageDialog(null,
                    "请预先设置登录信息，按进入保存并进入系统。\n各信息仅允许输入一次，请牢记用户名及密码！",
                    "没有登录配置", JOptionPane.WARNING_MESSAGE);

        setVisible(true);
    }

    /**
     * 初始化内部组件
     */
    private void initInternalComponent() {

        /* 欢迎标签 */
        JLabel welcomeLbl = new JLabel();
        welcomeLbl.setText("欢迎登陆身份采集系统");
        welcomeLbl.setFont(new Font(null, Font.BOLD, 20));
        //字体颜色设置
        welcomeLbl.setForeground(Color.RED);
        welcomeLbl.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel usrNamePanel = new JPanel(), pwdPanel = new JPanel();

        /*用户名和密码面板初始化*/
        JLabel usrNameLbl = new JLabel("用户名："), pwdLbl = new JLabel("　密码：");
        usrNamePanel.add(usrNameLbl);
        usrNamePanel.add(usrNameField);
        pwdPanel.add(pwdLbl);
        pwdPanel.add(pwdField);

        enterBtn.addActionListener(this);

        /*总体*/
        add(welcomeLbl);
        add(usrNamePanel);
        add(pwdPanel);
        add(enterBtn);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (enterBtn.equals(e.getSource())) {
            String userName = usrNameField.getText();
            String password = String.valueOf(pwdField.getPassword());
            if (checkLoginConfig()) {
                AdminInfo info = (AdminInfo) IO.file2Obj(CFG_FILENAME);
                assert info != null;
                if (info.getUserName().equals(userName) && info.getPassword().equals(password)) {
                    new DataManager();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "请重新输入登录信息", "用户名或者密码错误！", JOptionPane.ERROR_MESSAGE);
                    usrNameField.setText(null);
                    pwdField.setText(null);
                }
            } else {
                AdminInfo info = new AdminInfo();
                info.setUserName(userName);
                info.setPassword(password);
                if (IO.obj2File(CFG_FILENAME, info)) {
                    JOptionPane.showMessageDialog(this, "管理员登录信息已经保存。\n将退出程序，再次打开时请使用刚才设置的登录信息。");
                    System.exit(0);
                } else
                    JOptionPane.showMessageDialog(this,
                            String.format("请检查%s文件是否被占用或者驱动器是否可写", CFG_FILENAME),
                            "无法保存配置文件", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 检查登录配置
     *
     * @return {@link Boolean}
     */
    private boolean checkLoginConfig() {
        if (new File(CFG_FILENAME).exists()) {
            try {
                AdminInfo info = (AdminInfo) IO.file2Obj(CFG_FILENAME);
                return Objects.nonNull(info);
            } catch (ClassCastException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
