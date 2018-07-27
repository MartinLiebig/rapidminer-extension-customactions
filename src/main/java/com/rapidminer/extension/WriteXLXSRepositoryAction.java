package com.rapidminer.extension;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.operator.ProcessStoppedException;
import com.rapidminer.operator.io.ExcelExampleSetWriter;
import com.rapidminer.repository.Entry;
import com.rapidminer.repository.IOObjectEntry;
import com.rapidminer.repository.RepositoryException;
import com.rapidminer.repository.gui.RepositoryTree;
import com.rapidminer.repository.gui.actions.AbstractRepositoryAction;

import jxl.write.WriteException;


/**
 * Created by mschmitz on 21/03/2018.
 */
public class WriteXLXSRepositoryAction extends AbstractRepositoryAction<Entry> {

	public WriteXLXSRepositoryAction(RepositoryTree tree) {
		super(tree, Entry.class, false, "repository_write_xlsx");
	}


	@Override
	public void actionPerformed(Entry entry) {
		// Let the user choose  a file
		File f = SwingTools.chooseFile(null, null, false, null, null);
		// convert the Entry into an ExampleSet.
		// Since this action is only visible for Example Sets we do not need to take care of other IOObjects
		ExampleSet exampleSet = null;
		if (entry instanceof IOObjectEntry) {
			IOObjectEntry ioObjectEntry = (IOObjectEntry) entry;
			if (ExampleSet.class.isAssignableFrom(ioObjectEntry.getObjectClass())) {
				try {
					exampleSet = (ExampleSet) ioObjectEntry.retrieveData(null);
				} catch (RepositoryException e) {
					e.printStackTrace();
				}
			}
			// Write the file to disc using the operator
			try {
				FileOutputStream fos = new FileOutputStream(f);
				ExcelExampleSetWriter.writeXLSX(
						exampleSet,
						"Data",
						ExcelExampleSetWriter.DEFAULT_DATE_FORMAT,
						ExcelExampleSetWriter.DEFAULT_NUMBER_FORMAT,
						fos, null);
				fos.close();
			} catch (WriteException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ProcessStoppedException e) {
				e.printStackTrace();
			}
		}
	}
}
