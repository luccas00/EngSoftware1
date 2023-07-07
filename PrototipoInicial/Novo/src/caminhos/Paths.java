package caminhos;

import javax.swing.filechooser.FileSystemView;

public class Paths {
	
	/*A Classe PATHS auxilia o programa de Gerenciamento do Necroterio
	 * com os caminhos das pastas e arquivos de dados*/
	
	private static FileSystemView fileSystemView = FileSystemView.getFileSystemView();
	private static String desktopPath = fileSystemView.getHomeDirectory().getAbsolutePath();
	
	public static String getDesktopPath()
	{
		return desktopPath;
	}

	public static String getDataFolderPath()
	{
		return desktopPath + "/Data";
	}
	
	public static String getDataPath()
	{
		return desktopPath + "/Data/data.csv";
	}
	
}



