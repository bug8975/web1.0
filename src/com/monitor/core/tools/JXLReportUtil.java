package com.monitor.core.tools;

import java.io.File;

import com.monitor.core.constant.Globals;

import jxl.CellView;
import jxl.format.Alignment;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class JXLReportUtil {
    private static WritableCellFormat timesBoldCellFormat;
    private static WritableCellFormat times;
    private static WritableCellFormat timesAlignmentCentre;
    
    static {

		File dir = new File(Globals.REPORT_FILE_DIR);
		if(!dir.exists()){
			dir.mkdirs();
		}
		try {
			if(null == timesAlignmentCentre){
				// Lets create a times font
				WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
				// Define the cell format
				timesAlignmentCentre = new WritableCellFormat(times10pt);
				//centre alignment
				timesAlignmentCentre.setAlignment(Alignment.CENTRE);
				// Lets automatically wrap the cells
				timesAlignmentCentre.setWrap(true);
				timesAlignmentCentre.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
			}
			if(null == times){
				// Lets create a times font
				WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
				// Define the cell format
				times = new WritableCellFormat(times10pt);
				//left alignment
				times.setAlignment(Alignment.LEFT);
				// Lets automatically wrap the cells
				times.setWrap(true);
				times.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
			}
			if(null == timesBoldCellFormat){
				// create create a bold font with unterlines
				WritableFont times10ptBoldUnderline = new WritableFont(
						WritableFont.TIMES, 10, WritableFont.BOLD, false,
						UnderlineStyle.NO_UNDERLINE);
				timesBoldCellFormat = new WritableCellFormat(times10ptBoldUnderline);
				timesBoldCellFormat.setAlignment(Alignment.LEFT);
				// Lets automatically wrap the cells
				timesBoldCellFormat.setWrap(true);
				timesBoldCellFormat.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
			}
			CellView cv = new CellView();
			cv.setFormat(times);
			cv.setFormat(timesBoldCellFormat);
			cv.setFormat(timesAlignmentCentre);
			cv.setAutosize(true);
		} catch (WriteException e){
			Logger.printlnWithTime("ERROR: Cannot init the JXL WritableCellFormat" + e.getMessage());
			throw new RuntimeException(e);
		}
    }


	// for excel header
	public static void addHeaderLabel(WritableSheet sheet, int column, int row, String s)
			throws RowsExceededException, WriteException {
		Label label;
		label = new Label(column, row, s, timesBoldCellFormat);
		sheet.addCell(label);
	}
	
	// for excel string with alignment left by default
	public static void addStringLabel(WritableSheet sheet, int column, int row, String s)
			throws RowsExceededException, WriteException {
		Label label;
		label = new Label(column, row, s, times);
		sheet.addCell(label);
	}
	
	// for excel string with alignment centre
	public static void addStringLabelWithAlignmentCentre(WritableSheet sheet, int column, int row, String s)
			throws RowsExceededException, WriteException {
		Label label;
		label = new Label(column, row, s, timesAlignmentCentre);
		sheet.addCell(label);
	}

	// for excel number
	public static void addNumberLabel(WritableSheet sheet, int column, int row,
			double numberValue) throws WriteException, RowsExceededException {
		Number number;
		number = new Number(column, row, numberValue, times);
		sheet.addCell(number);
	}
}
