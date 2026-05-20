package com.delivery.util;

import java.io.IOException;
import java.util.List;

public interface Exportable<T> {
    void exportToCsv(List<T> items, String filePath) throws IOException;
    void exportToTxt(List<T> items, String filePath) throws IOException;
}
