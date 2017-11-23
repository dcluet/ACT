macro "Installation_2014_02_12"{

/*Major modifications:

Updates in comments
Comments in english

*/

selectWindow("Installation.ijm");			//Select the text window of the installation macro
run("Close");						//The text window is closed to simplify the interface 
requires("1.47s");					//Verification that the current version of imageJ is compatible

Error =0;						//Initialisation of the number of errors occurring during the installation process 

exec("open", "http://www.cecill.info/index.en.html");	//Open the web page for the CeCILL license

//License agreement window
//It is presented in loop until the user agrees to the license or click on cancel 

do{

Dialog.create("Instalation wizard for the ACT_Batch macro");
Dialog.addMessage("Version 1.2\n2013/09/30");
Dialog.addMessage("Copyright CNRS 2013\n \nCLUET David      david.cluet@ens-lyon.fr\nSTEBE Pierre Nicolas   pierre.stebe@ens-lyon.fr\nSPICHTY Martin   spichty.martin@ens-lyon.fr\nDELATTRE Marie   marie.delattre@ens-lyon.fr\n \nThis software is a computer program whose purpose is to automatically track centrosomes\nin DIC movies.\n \nThis software is governed by the CeCILL license under French law and abiding by the rules\nof distribution of free software. You can use, modify and/or redistribute the software\nunder the terms of the CeCILL license as circulated by CEA, CNRS and INRIA at the following URL\nhttp://www.cecill.info/index.en.html.\n \nAs a counterpart to the access to the source code and  rights to copy, modify and redistribute\ngranted by the license, users are provided only with a limited warranty  and the software's author,\nthe holder of the economic rights, and the successive licensors have only limited liability.\n \nIn this respect, the user's attention is drawn to the risks associated with loading, using, modifying\nand/or developing or reproducing the software by the user in light of its specific status of free\nsoftware, that may mean  that it is complicated to manipulate, and that also therefore means  that\nit is reserved for developers  and  experienced professionals having in-depth computer knowledge. Users\nare therefore encouraged to load and test the software's suitability as regards their requirements\nin conditions enabling the security of their systems and/or data to be ensured and, more generally,\nto use and operate it in the same conditions as regards security.\n \nThe fact that you are presently reading this means that you have had knowledge of the CeCILL license\nand that you accept its terms.\n");
Dialog.addMessage("");
Dialog.addCheckbox("   I agree to the terms of this license", 0);
Dialog.show();

L = Dialog.getCheckbox();	//Verify that the user agreed to the license

if (L==0){
	waitForUser("Please agree to the license terms to proceed.\nOtherwise press Cancel.");	//Display a message if the user didn't agree to the license
	
}

}while(L==0);

//End of the license agreement process.

PathSUM = getDirectory("macros")+File.separator+"StartupMacros.fiji.ijm";	//Retrieve the local path for the startupMacros.txt file
PathFolderInput =File.directory;					//Retrieve the path of the folder containing the installation macro.




PathMotor = PathFolderInput+ "Macro" +File.separator() +"ACT_Motor_CommandLine.ijm";			//Create the variable containing the path for the "ACT_Motor_CommandLine.txt" file (main program)
PathCommandLine = PathFolderInput+ "Macro" +File.separator() +"ACT_Table_CommandLine_creation.ijm";	//Create the variable containing the path for the "ACT_Table_CommandLine_creation.txt" file (GUI)
PathCommandSUM = PathFolderInput+ "Macro" +File.separator() +"CMD_SUM.ijm";				//Create the variable containing the path for the "CMD_SUM.txt" file (contains the code to be implemented in the startupmacros)
PathImage = PathFolderInput+ "Macro" +File.separator() +"ACT.jpg";					//Create the variable containing the path for the "ACT.jpg" file



//Verification that the installation folder contains the "ACT_Motor_CommandLine.txt" file.
if(File.exists(PathMotor)==0){
	waitForUser("File ACT_Motor_CommandLine.txt is missing");	//If the file is missing an error message is displayed, and the error counter is incremented.
	Error = Error+1;	
	}

//Verification that the installation folder contains the "ACT_Table_CommandLine_creation.txt" file.	
if(File.exists(PathCommandLine)==0){
	waitForUser("File ACT_Table_CommandLine_creation is missing"); //If the file is missing an error message is displayed, and the error counter is incremented.
	Error = Error+1;	
}

//Verification that the installation folder contains the "CMD_SUM.txt" file.	
if(File.exists(PathCommandSUM)==0){
	waitForUser("File CMD_SUM.txt is missing");	//If the file is missing an error message is displayed, and the error counter is incremented.
	Error = Error+1;
}
LigneC = File.openAsString(PathCommandSUM);		//Retrieve the code to be implemented in startupmacros.txt

//Verification that the installation folder contains the "ACT.jpg" file.	
if(File.exists(PathImage)==0){
	waitForUser("File ACT.jpg is missing"); //If the file is missing an error message is displayed, and the error counter is incremented.
	Error = Error+1;
}

//If errors are detected the process stops and the program prompts the user to verify the installation folder.
if(Error>0){
waitForUser("Files are missing!\nCheck your original Folder SASAM_Batch_1"); 
exit;
}

SUM = File.openAsString(PathSUM);	//Retrieve the content of startupmacros.txt in a string variable. 
pos =lastIndexOf(SUM, LigneC);		//Verify if a previous version of ACT is already installed (presence of the launching code in startupmacros)
if(pos == -1){
	SUM = SUM + "\n\n" + LigneC; 	//If it is the first installation, the launching code is added at the end of the startupmacros.
	Startup = File.open(PathSUM); 	//Startupmacros.txt is then updated and closed.
	print(Startup, SUM);			
	File.close(Startup);
}

PathOutput = getDirectory("macros")+File.separator()+"ACT_Batch";	//Create a string variable containing the path of the folder where the ACT files will be installed. 

//Verification that the ACT_Batch folder doesn't already exists.
//If the folder doesn't exist, it is created and the files are copied into it
if(File.exists(getDirectory("macros")+File.separator()+"ACT_Batch")==0){	
File.makeDirectory(PathOutput);
File.copy(PathMotor, PathOutput+File.separator+"ACT_Motor_CommandLine.txt");
File.copy(PathCommandLine, PathOutput+File.separator+"ACT_Table_CommandLine_creation.txt");
File.copy(PathImage, PathOutput+File.separator+"ACT.jpg");

//If the folder already exist, the files are copied into it
}else{
A=File.delete(PathOutput+File.separator+"ACT_Motor_CommandLine.txt");
B=File.delete(PathOutput+File.separator+"ACT_Table_CommandLine_creation.txt");
C=File.delete(PathOutput+File.separator+"ACT.jpg");
File.copy(PathMotor, PathOutput+File.separator+"ACT_Motor_CommandLine.txt");
File.copy(PathCommandLine, PathOutput+File.separator+"ACT_Table_CommandLine_creation.txt");
File.copy(PathImage, PathOutput+File.separator+"ACT.jpg");
}


//The program prompts the user that the process is over and requires to restart ImageJ to update the startupmacros and the associated shortcuts for ACT.
waitForUser("Installation has been performed sucessfully!\nRestart your ImageJ program.");

}
