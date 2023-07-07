package caminhos;

import javax.swing.filechooser.FileSystemView;

public class Paths {
	
	private static FileSystemView fileSystemView = FileSystemView.getFileSystemView();
	private static String desktopPath = fileSystemView.getHomeDirectory().getAbsolutePath();
	
	public static String getDesktopPath()
	{
		return desktopPath;
	}

	public static String getDataFolderString()
	{
		return desktopPath + "/Data";
	}
	
	public static String getDataPath()
	{
		return desktopPath + "/Data/data.csv";
	}
	
}



