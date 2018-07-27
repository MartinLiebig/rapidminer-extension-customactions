package com.rapidminer.extension;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.gui.tools.dialogs.DecisionRememberingConfirmDialog;
import com.rapidminer.repository.Entry;
import com.rapidminer.repository.Folder;
import com.rapidminer.repository.gui.RepositoryTree;
import com.rapidminer.repository.gui.actions.AbstractRepositoryAction;
import com.rapidminer.repository.local.LocalRepository;
import com.rapidminer.repository.local.SimpleFolder;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class OpenCustomFileBrowserAction extends AbstractRepositoryAction<Entry> {
	private static final long serialVersionUID = 1L;

	public OpenCustomFileBrowserAction(RepositoryTree tree) {
		super(tree, Entry.class, false, "repository_open_in_filebrowser_custom");
	}

	@Override
	public void actionPerformed(Entry entry) {
		if (entry == null || entry.getLocation() == null) {
			// should not happen
			return;
		}

		// check if user knows what he is doing
		if (!DecisionRememberingConfirmDialog.confirmAction("open_in_filebrowser",
				RapidMinerGUI.PROPERTY_OPEN_IN_FILEBROWSER)) {
			return;
		}


		try {
			if (entry.getLocation().getRepository() instanceof LocalRepository) {
				File repoRoot = ((LocalRepository) entry.getLocation().getRepository()).getRoot();
				if (repoRoot != null) {
					StringBuilder pathBuilder = new StringBuilder();
					LinkedList<String> listOfFolders = new LinkedList<>();
					Folder folder = entry instanceof SimpleFolder ? (SimpleFolder) entry : entry.getContainingFolder();
					pathBuilder.append(repoRoot.getAbsolutePath());

					// collect all parent folders until we reach the LocalRepository
					while (folder != null && !(folder instanceof LocalRepository)) {
						listOfFolders.add(folder.getName());
						folder = folder.getContainingFolder();
					}

					// iterate backwards over the folders so we can create the real path
					for (int i = listOfFolders.size() - 1; i >= 0; i--) {
						pathBuilder.append(File.separatorChar);
						pathBuilder.append(listOfFolders.get(i));
					}

					// try to open it if it exists and is a directory
					File file = new File(pathBuilder.toString());
					if (file.isDirectory() && file.exists()) {
						if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
							Desktop.getDesktop().open(file);
						} else {
							// read the preference from RapidMiner Studio
							// execute the command with the parameter file.
							return;
						}
					}
				}
			}
		} catch (IOException e) {
			// will appear on newer linux versions as the Desktop.open() call on a folder does not
			// work with current java versions
			SwingTools.showSimpleErrorMessage("cannot_open_in_filebrowser_io", "", entry.getLocation());
		} catch (Exception e) {
			SwingTools.showSimpleErrorMessage("cannot_open_in_filebrowser", e, entry.getLocation());
		}
	}
}
