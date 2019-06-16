package zx.ias.ui;

import zx.ias.bean.People;
import zx.ias.util.DebugLog;
import zx.ias.util.IO;
import zx.ias.util.UI;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

/**
 * DataManagerUI
 */

public class DataManager extends JFrame implements ActionListener, TableModelListener {

    /**
     * 数据文件
     */
    public static String CFG_FILENAME = "ias_data.dat";
    /**
     * 按键标和表头标
     */
    private final String addRc = "增加记录", delRc = "删除记录", qryRc = "查询记录", edtLI = "编辑登录信息", e2CSV = "导出CSV";
    private final String[] columnNames = {"姓名", "性别", "生日", "地址"};
    private DefaultTableModel tableModel;
    private JTable dataViewTable;
    private JButton addRecordBtn = new JButton(addRc),
            deleteRecordBtn = new JButton(delRc),
            queryRecordBtn = new JButton(qryRc),
            editAdminInfoBtn = new JButton(edtLI),
            export2CSVBtn = new JButton(e2CSV);

    /**
     * 如果有修改痕迹
     */
    private boolean hasModified = false;


    DataManager() {
        initInternalComponent();

        setTitle("数据管理器");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                /*如果发生修改，保存数据*/
                if (hasModified) {
                    switch (JOptionPane.showConfirmDialog(DataManager.this, "数据似乎已经经过修改，是否保存？", "保存确认", JOptionPane.YES_NO_CANCEL_OPTION)) {
                        case JOptionPane.YES_OPTION:
                            int row = dataViewTable.getRowCount();
                            People[] saveData = new People[row];
                            for (int i = 0; i < row; i++) {
                                saveData[i] = getPeopleDataFromTable(i);
                            }
                            if (IO.obj2File(CFG_FILENAME, saveData))
                                JOptionPane.showMessageDialog(null, "保存成功！", null, JOptionPane.INFORMATION_MESSAGE);
                            else
                                JOptionPane.showMessageDialog(null, "保存失败！", null, JOptionPane.ERROR_MESSAGE);
                            dispose();
                            break;
                        case JOptionPane.NO_OPTION:
                            dispose();
                            break;
                        case JOptionPane.CANCEL_OPTION:
                            //取消不关闭数据管理器
                            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                            break;
                    }
                }
            }
        });
        setVisible(true);
    }

    private void initInternalComponent() {

        People[] data = null;

        try {
            data = (People[]) IO.file2Obj(CFG_FILENAME);
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            /*数据第一次加载*/
            if (data == null) {
                hasModified = true;
                JOptionPane.showMessageDialog(this, "将加载预设的数据（参考模板）...", "数据文件未就绪", JOptionPane.INFORMATION_MESSAGE);
                data = new People[]{
                        /*数据来源：EsuWiki*/
                        new People("董旭阳", People.SexType.MALE, LocalDate.of(1982, 7, 15), "福建惠安"),
                        new People("陈乾", People.SexType.MALE, LocalDate.of(2002, 4, 18), "广东省普宁市下架山镇土坑村北片45号"),
                        new People("吴周星", People.SexType.FEMALE, LocalDate.parse("2003-06-28"), "重庆市巴南区万达华城15栋28楼2号"),
                        new People("庄海洋", People.SexType.MALE, LocalDate.ofYearDay(2001, 1), "山东省日照市东港区海曲西路155号"),
                        new People("胡诗文", People.SexType.FEMALE, LocalDate.of(1991, 2, 3), "江苏省苏州市姑苏区竹辉路298号"),
                        new People()};
            }
        }

        /*浏览用数据准备*/
        String[][] viewData = new String[data.length][columnNames.length];
        for (int i = 0; i < data.length; i++) {
            People people = data[i];
            viewData[i][0] = people.getName();
            viewData[i][1] = People.getReadableSexString(people.getSex());
            viewData[i][2] = people.getBirthday().toString();
            viewData[i][3] = people.getAddress();
        }

        tableModel = new DefaultTableModel(viewData, columnNames);

        /*创建表格组件时禁用表格内编辑*/
        dataViewTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        /*启用双击编辑*/
        dataViewTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //双击：左键点击两次
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                    int selectedRow = dataViewTable.getSelectedRow();
                    People people = getPeopleDataFromTable(selectedRow);
                    new DataEditor(dataViewTable, people, selectedRow);
                }
            }
        });
        dataViewTable.getModel().addTableModelListener(this);

        dataViewTable.getTableHeader().setReorderingAllowed(false); //不可整列移动

        /*按钮添加动作监听器*/
        addRecordBtn.addActionListener(this);
        deleteRecordBtn.addActionListener(this);
        editAdminInfoBtn.addActionListener(this);
        queryRecordBtn.addActionListener(this);
        export2CSVBtn.addActionListener(this);

        /*按钮添加工具提示*/
        addRecordBtn.setToolTipText("无选中行时在表数据末尾添加一条初始记录，有选中行时将在选中行之后添加");
        deleteRecordBtn.setToolTipText("删除选中行记录");
        queryRecordBtn.setToolTipText("根据包含的文本查询特定记录");
        editAdminInfoBtn.setToolTipText("修改管理用户信息（用户名和密码）");
        export2CSVBtn.setToolTipText("将数据导出成csv文件，使其他工具软件（如Excel等）可以利用数据");

        UI.fitTableColumns(dataViewTable);

        /*按钮添加进面板*/
        JPanel buttonPanel = new JPanel();
        //提示文字边框
        buttonPanel.setBorder(BorderFactory.createTitledBorder("操作按钮（悬停查看帮助说明）"));
        buttonPanel.add(addRecordBtn);
        buttonPanel.add(deleteRecordBtn);
        buttonPanel.add(queryRecordBtn);
        buttonPanel.add(editAdminInfoBtn);
        buttonPanel.add(export2CSVBtn);


        JPanel tablePanel = new JPanel();
        //滚动面板
        JScrollPane scrollPane = new JScrollPane(dataViewTable);
        tablePanel.setBorder(BorderFactory.createTitledBorder("数据浏览（双击编辑）"));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        tablePanel.add(scrollPane);

        /*mainPanel设置纵向布局*/
        JPanel mainPanel = new JPanel();
        mainPanel.add(tablePanel);
        mainPanel.add(buttonPanel);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JButton) {
            switch (((JButton) e.getSource()).getText()) {
                case addRc:
                    People people = new People();
                    int selectedRow = dataViewTable.getSelectedRow();
                    Object[] insertData =
                            new Object[]{people.getName(), People.getReadableSexString(people.getSex()), people.getBirthday(), people.getAddress()};
                    if (selectedRow == -1) tableModel.addRow(insertData);
                    else {
                        /*由于索引不断改变，因此必须反向增加*/
                        var rowsMark = new ArrayList<Integer>();
                        for (int i : dataViewTable.getSelectedRows()) rowsMark.add(i);
                        Collections.reverse(rowsMark);
                        for (Object i : rowsMark.toArray()) {
                            DebugLog.i("Add row: Add a new row after ROW:" + i);
                            tableModel.insertRow((int) i + 1, insertData);
                        }
                    }
                    break;
                case delRc:
                    for (int length = dataViewTable.getSelectedRowCount(); length > 0; length--) {
                        int row = dataViewTable.getSelectedRow();
                        DebugLog.i("Delete row: Len=" + length + ", Row=" + row);
                        tableModel.removeRow(row);
                    }
                    break;
                case qryRc:
                    String input = JOptionPane.showInputDialog("请输入包含于欲查找数据的文本（以单元格为单位查找）：");
                    if (input == null) break;
                    StringBuilder builder = new StringBuilder();
                    builder.append("匹配的数据在： \n");
                    boolean isSelected = false, hasResult = false;
                    for (int i = 0; i < dataViewTable.getRowCount(); i++) {
                        for (int j = 0; j < dataViewTable.getColumnCount(); j++) {
                            if (String.valueOf(dataViewTable.getValueAt(i, j)).contains(input)) {
                                hasResult = true;
                                if (!isSelected) dataViewTable.setRowSelectionInterval(i, i);
                                isSelected = true;
                                builder.append(String.format("第%d行，第%d列", i + 1, j + 1))
                                        .append(System.lineSeparator());
                            }
                        }
                    }
                    builder.append("首个匹配行已经自动选中");
                    if (hasResult) JOptionPane.showMessageDialog(this, builder);
                    else JOptionPane.showMessageDialog(this, "查无结果", null, JOptionPane.ERROR_MESSAGE);
                    break;
                case edtLI:
                    new AdminEditor();
                    break;
                case e2CSV:
                    DebugLog.i("Option:save to CSV");
                    var fileChooser = new JFileChooser(".");
                    fileChooser.setFileFilter(new FileNameExtensionFilter("逗号分隔值文件", "csv"));
                    int ret = fileChooser.showSaveDialog(this);
                    if (ret == JFileChooser.APPROVE_OPTION) {
                        var path = fileChooser.getSelectedFile().toString();
                        if (!path.endsWith(".csv"))
                            path += ".csv";
                        //uFEFF:Windows识别UTF-8所带的BOM头
                        StringBuilder csvBody = new StringBuilder(
                                '\uFEFF' + String.join(",", columnNames) + System.lineSeparator());
                        DebugLog.i("Write CSV to " + path);
                        int row = dataViewTable.getRowCount();
                        var saveData = new People[row];
                        for (int i = 0; i < row; i++) {
                            saveData[i] = getPeopleDataFromTable(i);
                        }
                        for (People p : saveData) {
                            csvBody.append(String.join(",", new String[]{
                                    p.getName(),
                                    People.getReadableSexString(p.getSex()),
                                    p.getBirthday().toString(),
                                    p.getAddress()
                            })).append(System.lineSeparator());
                        }
                        IO.writeTxtFile(csvBody.toString(), path);
                        JOptionPane.showMessageDialog(this, "已经保存" + System.lineSeparator() + path);
                    } else {
                        JOptionPane.showMessageDialog(this, "出现错误或者取消了保存。");
                    }
                    break;
            }
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (!hasModified) hasModified = true;
    }

    /**
     * 从tableModel中获取People数据
     *
     * @param rowIndex 行索引
     * @return {@link People}
     */
    private People getPeopleDataFromTable(int rowIndex) {
        return new People((String) tableModel.getValueAt(rowIndex, 0)
                , People.getSexTypeFromString((String) tableModel.getValueAt(rowIndex, 1))
                , LocalDate.parse(String.valueOf(tableModel.getValueAt(rowIndex, 2)))
                , (String) tableModel.getValueAt(rowIndex, 3));
    }
}
