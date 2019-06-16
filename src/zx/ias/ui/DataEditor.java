package zx.ias.ui;

import zx.ias.util.DebugLog;
import zx.ias.bean.People;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;

/**
 * DataEditorUI
 */

public class DataEditor extends JDialog implements ActionListener {

    private static final int TEXT_FIELD_COL = 40;

    private People mData;
    private JTable mDataViewTable;

    private int index;

    private JTextField nameField, birthdayField, addressField;

    private JRadioButton sexMaleBtn = new JRadioButton("男"), sexFemaleBtn = new JRadioButton("女");
    private JButton openDataChooserBtn = new JButton("打开日期选择器"), saveBtn = new JButton("保存");

    DataEditor(JTable dataViewTable, People data, int index) {

        mData = data;
        mDataViewTable = dataViewTable;
        this.index = index;

        //this.getRootPane().setWindowDecorationStyle(JRootPane.INFORMATION_DIALOG);

        setTitle("数据编辑器　行索引:" + (index + 1));
        setLayout(new GridLayout(data.getClass().getDeclaredFields().length, 1));
        //相对与表格位置开起来更自然
        setLocationRelativeTo(mDataViewTable);
        initInternalComponent();
        pack();
        setResizable(false);
        setVisible(true);
    }


    private void initInternalComponent() {

        JLabel nameLbl = new JLabel("姓名："), sexLbl = new JLabel("性别："),
                birthdayLbl = new JLabel("生日："), addressLbl = new JLabel("地址：");

        JPanel namePanel = new JPanel(),
                sexPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)),
                birthdayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)), addressPanel = new JPanel();

        nameField = new JTextField(TEXT_FIELD_COL);
        birthdayField = new JTextField();
        addressField = new JTextField(TEXT_FIELD_COL);

        birthdayField.setEnabled(false);

        /*性别组*/
        ButtonGroup sexGroup = new ButtonGroup();
        sexGroup.add(sexMaleBtn);
        sexGroup.add(sexFemaleBtn);

        /*组件状态初始化*/
        nameField.setText(mData.getName());
        birthdayField.setText(mData.getBirthday().toString());
        switch (mData.getSex()) {
            case MALE:
                sexMaleBtn.setSelected(true);
                break;
            case FEMALE:
                sexFemaleBtn.setSelected(true);
                break;
        }
        addressField.setText(mData.getAddress());

        /*面板*/
        namePanel.add(nameLbl);
        namePanel.add(nameField);
        sexPanel.add(sexLbl);
        sexPanel.add(sexMaleBtn);
        sexPanel.add(sexFemaleBtn);
        birthdayPanel.add(birthdayLbl);
        birthdayPanel.add(birthdayField);
        birthdayPanel.add(openDataChooserBtn);
        addressPanel.add(addressLbl);
        addressPanel.add(addressField);

        /*动作*/
        openDataChooserBtn.addActionListener(this);
        saveBtn.addActionListener(this);

        /*总体*/
        add(namePanel);
        add(sexPanel);
        add(birthdayPanel);
        add(addressPanel);
        add(saveBtn);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(openDataChooserBtn)) {
            new DateChooser(mData.getBirthday(), date -> {
                DebugLog.i("DateChooser return value:" + date);
                birthdayField.setText(date.toString());
            });
        } else if (e.getSource().equals(saveBtn)) {
            /*用改变数据模型的方法返回值*/
            TableModel model = mDataViewTable.getModel();
            model.setValueAt(nameField.getText(), index, 0);
            model.setValueAt(sexMaleBtn.isSelected() ? "男" : "女", index, 1);
            model.setValueAt(LocalDate.parse(birthdayField.getText()), index, 2);
            model.setValueAt(addressField.getText(), index, 3);
            dispose();
        }
    }
}
