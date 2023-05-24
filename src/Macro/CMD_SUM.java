//A.C.T. 2014/02/13

//Shortcut for the ACT GUI and main program
macro "A.C.T."{
	path = getDirectory('macros') + File.separator();
	path += 'ACT_Batch' + File.separator(); 
	path += 'ACT_Table_CommandLine_creation.java';

	//Launch the GUI of ACT for the pre-treatment of the movies
	runMacro(path); 
}


//Shortcut to launch a set of manually created command lines in batch-mode,
//skipping the GUI.
//The command lines can have different resolutions, extension,...
macro "A.C.T. Command-lines LAUNCHER"{

	//CeCill license window	
	PathImage = getDirectory('macros') + File.separator();
	PathImage += 'ACT_Batch' + File.separator() + 'ACT.jpg';
	open(PathImage);

	T = getTitle();
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
	msg += ' and INRIA at the following';
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
	msg += 'that you accept its terms.\n'
	
	waitForUser(msg);
	selectWindow(T);
	close();

	//Dialog interface. 
	//The user specify the path of the txt file containing the command lines.
	P = File.openDialog('');
		//Extract the command lines from the specified file.
		Listing = split(File.openAsString(P),'\n');

		//All the command lines are sequentially executed.
		for(z=1; z<lengthOf(Listing); z++){
			Arguments = Listing[z];
			if(Listing[z] != ''){
				path = getDirectory('macros') + File.separator();
				path += 'ACT_Batch' + File.separator();
				path += 'ACT_Motor_CommandLine.java'
				runMacro(path, Arguments);
			}
		}
}
