package zx.ias.ui;

import zx.ias.util.DebugLog;
import zx.ias.util.IO;
import zx.ias.bean.AdminInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 * AdminEditorUI
 */

public class AdminEditor extends JFrame implements ActionListener {

    private static final int TEXT_FIELD_COL = 30;

    private JTextField usrNameField;
    private JPasswordField oldPwdField, newPwdField, newPwdConfirmField;
    private JButton confirmBtn;

    private AdminInfo oldInfo;

    AdminEditor() {
        initInternalComponent();

        setLayout(new GridLayout(5, 1));
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }


    private void initInternalComponent() {
        usrNameField = new JTextField(TEXT_FIELD_COL);
        oldPwdField = new JPasswordField(TEXT_FIELD_COL);
        newPwdField = new JPasswordField(TEXT_FIELD_COL);
        newPwdConfirmField = new JPasswordField(TEXT_FIELD_COL);

        confirmBtn = new JButton("确认");

        JPanel namePanel = new JPanel();
        namePanel.add(new JLabel("用户名："));
        namePanel.add(usrNameField);

        JPanel oldPwdPanel = new JPanel();
        oldPwdPanel.add(new JLabel("旧密码："));
        oldPwdPanel.add(oldPwdField);

        JPanel newPwdPanel = new JPanel();
        newPwdPanel.add(new JLabel("新密码："));
        newPwdPanel.add(newPwdField);

        JPanel newPwdCPanel = new JPanel();
        newPwdCPanel.add(new JLabel("再确认："));
        newPwdCPanel.add(newPwdConfirmField);

        oldInfo = (AdminInfo) IO.file2Obj(Login.CFG_FILENAME);
        usrNameField.setText(oldInfo != null ? oldInfo.getUserName() : "");

        confirmBtn.addActionListener(this);

        add(namePanel);
        add(oldPwdPanel);
        add(newPwdPanel);
        add(newPwdCPanel);
        add(confirmBtn);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(confirmBtn)) {
            /*密码检查*/
            if (!Arrays.equals(newPwdField.getPassword(), newPwdConfirmField.getPassword())) {
                DebugLog.e(Arrays.toString(newPwdField.getPassword()) + " != " + Arrays.toString(newPwdConfirmField.getPassword()));
                JOptionPane.showMessageDialog(this, "新密码两次输入不一致！", null, JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!oldInfo.getPassword().equals(String.valueOf(oldPwdField.getPassword()))) {
                DebugLog.e("oldPwd Error! " + oldInfo.getPassword() + " & " + Arrays.toString(oldPwdField.getPassword()));
                JOptionPane.showMessageDialog(this, "旧密码不匹配！", null, JOptionPane.ERROR_MESSAGE);
                return;
            }
            AdminInfo info = new AdminInfo();
            info.setUserName(usrNameField.getText());
            info.setPassword(String.valueOf(newPwdField.getPassword()));
            DebugLog.i("change admin info:" + IO.obj2File(Login.CFG_FILENAME, info));
            JOptionPane.showMessageDialog(this, "下次启动请使用新的用户信息登录！", "已经尝试保存登录信息...", JOptionPane.WARNING_MESSAGE);
            dispose();
        }
    }
}
