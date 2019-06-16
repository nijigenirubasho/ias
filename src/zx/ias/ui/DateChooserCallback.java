package zx.ias.ui;

import java.time.LocalDate;

public interface DateChooserCallback {
    void onDateChosen(LocalDate date);
}