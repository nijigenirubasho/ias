package zx.ias.util;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.util.Enumeration;

/**
 * UIUtil
 */

public class UI {

    /**
     * 禁止实例化
     */
    private UI() {
    }

    /**
     * 使用系统默认UI风格
     */
    public static void configSystemDefaultUIStyle() {
        try {
            String className = UIManager.getSystemLookAndFeelClassName();
            DebugLog.i(className);
            UIManager.setLookAndFeel(className);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    /**
     * 表格宽度自适应，注意先装填好数据
     *
     * @param myTable {@link JTable}
     */
    public static void fitTableColumns(JTable myTable) {
        JTableHeader header = myTable.getTableHeader();
        int rowCount = myTable.getRowCount();
        //在Windows风格上有遮挡的可能，所以设置保留宽度
        int reserveWidth = 5;
        Enumeration columns = myTable.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn column = (TableColumn) columns.nextElement();
            int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
            int width = (int) myTable.getTableHeader().getDefaultRenderer()
                    .getTableCellRendererComponent(myTable, column.getIdentifier()
                            , false, false, -1, col).getPreferredSize().getWidth();
            for (int row = 0; row < rowCount; row++) {
                int preferredWidth = (int) myTable.getCellRenderer(row, col).getTableCellRendererComponent(myTable,
                        myTable.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
                width = Math.max(width, preferredWidth);
            }
            header.setResizingColumn(column);
            column.setWidth(width + myTable.getIntercellSpacing().width + reserveWidth);
        }
    }
}
