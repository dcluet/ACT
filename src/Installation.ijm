macro "Installation_2023_05_12"{

/*Major modifications:

Updates in comments
Comments in english

*/

// Verification that the current version of imageJ is compatible
requires('1.47s');

// Initialisation of the number of errors occurring during the 
// installation process 
Error = 0;						

// License agreement window
// It is presented in loop until the user agrees to the license or click on cancel 

do{
	msg = 'Copyright CNRS 2013\n \n';
	msg += 'CLUET David      david.cluet@ens-lyon.fr\n';
	msg += 'STEBE Pierre Nicolas   pierre.stebe@ens-lyon.fr\n';
	msg += 'SPICHTY Martin   spichty.martin@ens-lyon.fr\n';
	msg += 'DELATTRE Marie   marie.delattre@ens-lyon.fr\n \n';
	msg += 'This software is a computer program whose purpose is to ';
	msg += 'automatically track centrosomes\nin DIC movies.\n \n';
	msg += 'This software is governed by the CeCILL license under French law ';
	msg += 'and abiding by the rules\nof distribution of free software. ';
	msg += 'You can use, modify and/or redistribute the software\n';
	msg += 'under the terms of the CeCILL license as circulated by CEA, CNRS';
	msg += ' and INRIA at the following ';
	msg += 'URL\nhttp://www.cecill.info/index.en.html.\n \n';
	msg += 'As a counterpart to the access to the source code and  rights to ';
	msg += 'copy, modify and redistribute\ngranted by the license, users are ';
	msg += 'provided only with a limited warranty  and the software s author,';
	msg += '\nthe holder of the economic rights, and the successive licensors ';
	msg += 'have only limited liability.\n \nIn this respect, the user s ';
	msg += 'attention is drawn to the risks associated with loading, using, ';
	msg += 'modifying\nand/or developing or reproducing the software by the ';
	msg += 'user in light of its specific status of free\nsoftware, that may ';
	msg += 'mean  that it is complicated to manipulate, and that also ';
	msg += 'therefore means  that\nit is reserved for developers  and  ';
	msg += 'experienced professionals having in-depth computer knowledge. ';
	msg += 'Users\nare therefore encouraged to load and test the software s ';
	msg += 'suitability as regards their requirements\nin conditions enabling ';
	msg += 'the security of their systems and/or data to be ensured and, more ';
	msg += 'generally,\nto use and operate it in the same conditions as ';
	msg += 'regards security.\n \nThe fact that you are presently reading ';
	msg += 'this means that you have had knowledge of the CeCILL license\nand ';
	msg += 'that you accept its terms.\n';

	Dialog.create('Instalation wizard for the ACT_Batch macro');
	Dialog.addMessage('Version 1.3\n2023_05_12');
	Dialog.addMessage(msg);
	Dialog.addMessage('');
	Dialog.addCheckbox('   I agree to the terms of this license', 0);
	Dialog.show();

	// Verify that the user agreed to the license
	L = Dialog.getCheckbox();	

	if (L == 0){
		// Display a message if the user didn't agree to the license
		warn = 'Please agree to the license terms to proceed.';
		warn += '\nOtherwise press Cancel.';
		waitForUser(warn);	
	}
} while(L==0);

// End of the license agreement process.
// Retrieve the local path for the startupMacros.txt file
PathSUM = getDirectory('macros') + File.separator() + 'StartupMacros.txt';

// Create the variable containing the path for the "ACT_Motor_CommandLine.java" 
// file (main program)
PathMotor = PathFolderInput + 'Macro' + File.separator();
PathMotor += 'ACT_Motor_CommandLine.java';

// Create the variable containing the path for the 
// "ACT_Table_CommandLine_creation.java" file (GUI)

PathCommandLine = PathFolderInput + 'Macro' +File.separator();
PathCommandLine += 'ACT_Table_CommandLine_creation.java';

// Create the variable containing the path for the "CMD_SUM.java" file 
// (contains the code to be implemented in the startupmacros)
PathCommandSUM = PathFolderInput + 'Macro' + File.separator() + 'CMD_SUM.java';

//Create the variable containing the path for the "ACT.jpg" file
PathImage = PathFolderInput + 'Macro' + File.separator() + 'ACT.jpg';

// Verification that the installation folder contains the 
// "ACT_Motor_CommandLine.java" file.
if(File.exists(PathMotor) == 0){
	// If the file is missing an error message is displayed,
	// and the error counter is incremented.
	waitForUser('File ACT_Motor_CommandLine.java is missing');
	Error = Error + 1;	
	}

// Verification that the installation folder contains the
// "ACT_Table_CommandLine_creation.java" file.	
if(File.exists(PathCommandLine) == 0){
	// If the file is missing an error message is displayed, and the error
	// counter is incremented.
	waitForUser('File ACT_Table_CommandLine_creation.java is missing');
	Error = Error + 1;	
}

// Verification that the installation folder contains the "CMD_SUM.txt" file.	
if(File.exists(PathCommandSUM) == 0){
	// If the file is missing an error message is displayed,
	// and the error counter is incremented.
	waitForUser('File CMD_SUM.java is missing');
	Error = Error + 1;
}

// Retrieve the code to be implemented in startupmacros.txt
LigneC = File.openAsString(PathCommandSUM);

// Verification that the installation folder contains the "ACT.jpg" file.	
if(File.exists(PathImage) == 0){
	// If the file is missing an error message is displayed, and the error
	// counter is incremented.
	waitForUser('File ACT.jpg is missing');
	Error = Error + 1;
}

// If errors are detected the process stops and the program prompts the user 
// to verify the installation folder.
if(Error > 0){
waitForUser('Files are missing!\nCheck your ACT/src folder'); 
exit();
}

// Retrieve the content of startupmacros.txt in a string variable.
SUM = File.openAsString(PathSUM);

// Verify if a previous version of ACT is already installed (presence of the
// launching code in startupmacros)
pos = lastIndexOf(SUM, LigneC);
if(pos == -1){
	// If it is the first installation, the launching code is added at the end
	// of the startupmacros.
	SUM = SUM + '\n\n' + LigneC;

	//Startupmacros.txt is then updated and closed.	
	Startup = File.open(PathSUM);
	print(Startup, SUM);			
	File.close(Startup);
}

// Create a string variable containing the path of the folder where the
// ACT files will be installed. 
PathOutput = getDirectory('macros') + File.separator() + 'ACT_Batch';

// Verification that the ACT_Batch folder doesn't already exists.
// If the folder doesn't exist, it is created and the files are copied into it
if(File.exists(PathOutput) == 0){	
	File.makeDirectory(PathOutput);
	File.copy(PathMotor,
			  PathOutput + File.separator() + 'ACT_Motor_CommandLine.java');
	File.copy(PathCommandLine,
			  PathOutput + File.separator() + 'ACT_Table_CommandLine_creation.java');
	File.copy(PathImage,
			  PathOutput + File.separator() + 'ACT.jpg');

//If the folder already exist, the files are copied into it
}else{
	A = File.delete(PathOutput + File.separator() + 'ACT_Motor_CommandLine.java');
	B = File.delete(PathOutput + File.separator() + 'ACT_Table_CommandLine_creation.java');
	C = File.delete(PathOutput + File.separator() + 'ACT.jpg');
	File.copy(PathMotor,
			PathOutput + File.separator() + 'ACT_Motor_CommandLine.java');
	File.copy(PathCommandLine,
			PathOutput + File.separator() + 'ACT_Table_CommandLine_creation.java');
	File.copy(PathImage,
			PathOutput + File.separator() + 'ACT.jpg');
}

// The program prompts the user that the process is over and requires to 
// restart ImageJ to update the startupmacros and the associated shortcuts
// for ACT.

msg = 'Installation has been performed sucessfully!\n';
msg += 'Restart your ImageJ program.';
waitForUser(msg);

}