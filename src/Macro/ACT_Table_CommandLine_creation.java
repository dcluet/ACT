macro "CommandLineCreation"{

	Version = "1.32_2023_05_12";

	/*Major modifications:

	Updates in comments
	Comments in english
	Explicit naming of hbb and dsc in the GUI

	*/

	//CeCILL Lincense window 
	PathImage = getDirectory('macros') +File.separator() +'ACT_Batch';
	PathImage += File.separator + 'ACT.jpg';
	open(PathImage);

	// Get the image title
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
	msg += 'that you accept its terms.\n';

	waitForUser(msg);
	selectWindow(T);
	close();

	// Default parameters to feed the GUI
	// movie extension
	RefExt =".mov";
	// Advanced mode "On"
	Mode = 1;
	// resolution um/pix
	Reso = 0.0530001;
	// time gap between 2 images
	t = 0.5;
	// Direction of analysis for the anterior centrosome (FORWARD)
	SensAnt = 1;
	// Boundary box type for the anterior centrosome (RETROGRADE)
	BoxAnt = 1;
	// Diameter of the scanning circle for the anterior centrosome (dsc)
	DCAnt = 1.25;
	// Width of the focus square used for the anterior centrosome
	LFAnt = 1;
	// Height of the boundary box for the anterior centrosome (hbb)
	LPAnt = 1.125;
	// Direction of analysis for the posterior (REVERSE)
	SensPost = 0;
	// Boundary box type for the posterior centrosome (ANTEROGRADE)
	BoxPost = 0;
	// Diameter of the scanning circle for the posterior centrosome (dsc)
	DCPost = 1.125;
	// Width of the focus square used for the posterior centrosome
	LFPost = 1;
	// Height of the boundary box for the posterior centrosome (hbb)
	LPPost = 1.375;

	// Calling the "Principal" function with default parameters and retrieving
	// user's adjusted setting as a string (R)
	R = Principal(1.3,
				  RefExt,
				  Mode,
				  Reso,
				  t,
				  SensAnt, BoxAnt, DCAnt, LFAnt, LPAnt,
				  SensPost, BoxPost, DCPost, LFPost, LPPost);

	// All the user's adjusted parameters are extracted from the string R 
	R2 = split(R, "\t");	

	// movie extension
	RefExt = R2[0];
	// Advanced mode
	Mode = parseFloat(R2[1]);
	// resolution um/pix
	Reso = parseFloat(R2[2]);
	// time gap between 2 images
	t = parseFloat(R2[3]);
	// Direction of analysis for the anterior centrosome
	SensAnt = parseFloat(R2[4]);
	// Boundary box type for the anterior centrosome
	BoxAnt = parseFloat(R2[5]);
	// Diameter of the scanning circle for the anterior centrosome (dsc)
	DCAnt = parseFloat(R2[6]);
	// Height of the boundary box for the anterior centrosome (hbb)
	LPAnt = parseFloat(R2[7]);
	// Direction of analysis for the posterior
	SensPost = parseFloat(R2[8]);
	// Boundary box type for the posterior centrosome
	BoxPost = parseFloat(R2[9]);
	// Diameter of the scanning circle for the posterior centrosome (dsc)
	DCPost = parseFloat(R2[10]);
	// Height of the boundary box for the posterior centrosome (hbb)
	LPPost = parseFloat(R2[11]);

	/*
	Dialog window allowing the user to indicate the location of the root folder 
	containing all the movies to process. Downstream folders will all be 
	analyzed to find movies with the correct extension.
	*/
	dir = getDirectory('Folder');

	//Empty the log window
	print('');
	print('\\Clear');
	
	// Exploration of the folder and creation of the list of movies using the
	// "listFiles function"
	listFiles(dir, RefExt); 
	
	// Save the paths of the movies in the "ListMovies.txt" file
	selectWindow('Log');
	NomRapport = '' + dir + 'ListMovies.txt';
	saveAs('Text', NomRapport); 

	// Empty and close the Log window
	print('\\Clear');
	run('Close');

	// Retrieve the list of movie from the "ListMovies.txt" file
	RefMovies = File.openAsString(dir + 'ListMovies.txt');

	//If the list is empty the program alerts the user and exits.
	if (RefMovies == ''){
		Dialog.create('ERROR');
		warn = 'No movie with the ' + RefExt;
		warn += ' extension was fou nd\nin the folder:\n' + dir;
		Dialog.addMessage();
		Dialog.show();
		exit();
	}

	// Extract all the paths of the movies
	ListMovies = split(File.openAsString(dir + 'ListMovies.txt'), '\n');

	// Create a list with the movie names for the GUI.
	ListInput = newArray(lengthOf(ListMovies));
	for (m=0; m<lengthOf(ListMovies); m++){
		ListInput[m] = File.getName(ListMovies[m]);
	}

	if (File.exists(dir +'ListCommands.txt') == 0){
		// Creation of the output File if not existing
		CLS = File.open(dir + 'ListCommands.txt');
		print(CLS, '');
		File.close(CLS);
	}else{
		// Retrieve the names of the movies already processed
		// (for multi-session pre-treatment).
		DejaFait = File.openAsString(dir + 'ListCommands.txt');
		ListingFait = split(DejaFait, '\n');

		// Compare the list of movies found and the list of 
		// movies already pre-treated
		for(i=0; i<lengthOf(ListMovies); i++){
			for(j=0; j<lengthOf(ListingFait); j++){
				if (ListingFait[j] != ''){
					Ligne = split(ListingFait[j], '\t');
					// Remove from the ListMovies array the pre-teated ones.					
					if (Ligne[0] == ListMovies[i]){	
						ListMovies[i] = '';
						j = lengthOf(ListingFait) + 1;
					}
				}
			}
		}
	}

	// Creation of the listing of pre-treated movie for the GUI.
	DejaFait = File.openAsString(dir + 'ListCommands.txt');
	ListingFait = split(DejaFait, '\n');
	n = lengthOf(ListingFait);
	ListOutput = newArray(lengthOf(ListingFait) - 1);
	nFilm = 0;

	// If some movies have already been pre-treated...
	if(n > 0){
		for (m=1; m<lengthOf(ListingFait); m++){
			if(ListingFait[m] != ''){
				// add the pre-treated movie name to the GUI listing.
				nFilm = nFilm + 1;
				Ligne = split(ListingFait[m], '\t');
				ListOutput[m-1] = File.getName(Ligne[0]);
			}
		}

	}else{
		// If no movie have been pre-treated the GUI listing is empty.
		ListOutput = newArray('None');
	}

	if(lengthOf(ListOutput) == 0){
		ListOutput = newArray('None');
	}

	// User interface indicatES the number of identified movies to treat,
	// those that are already pre-treated and option for reseting command lines.
	OptionErase = newArray('Proceed', 'Reset Command lines');

	Dialog.create('End of the exploration of the root folder');
	msg1 = 'The program has found ' + lengthOf(ListMovies);
	msg1 += ' movies\nwith the ' + RefExt + ' extension.';
	Dialog.addMessage(msg1);
	Dialog.addChoice('Movie list:', ListInput);
	Dialog.addMessage('' + nFilm + ' movies have already been pre-treated.');
	Dialog.addChoice('Movie list:', ListOutput);
	msg2 = 'You may want reseting the analysis parameters.\n';
	msg2 += 'Thus choose the reseting option.';
	Dialog.addMessage(msg2);
	Dialog.addChoice('Reseting option:', OptionErase);
	Dialog.show();

	// a and b are not used (the lists are just here for information). 
	// But they need to be fed so E can be properly fed with the
	// reseting option.
	a = Dialog.getChoice();
	b = Dialog.getChoice();
	E = Dialog.getChoice();

	// Reseting the command lines if chosen by the user.
	if (E == 'Reset Command lines'){
		CLS = File.open(dir + 'ListCommands.txt');
		print(CLS, '');
		File.close(CLS);

		CLS = File.open(dir + 'ListMovies.txt');
		print(CLS, RefMovies);
		File.close(CLS);
		ListMovies = split(File.openAsString(dir + 'ListMovies.txt'), '\n');
	}

	/*
	LOOP FOR MOVIE PRE-TREATMENT
	The listing is contained in the ListMovies array
	The array contains empty slots corresponding to the pre-treated movies,
	except if user has reset the commandline.
	If all movies have been pre-treated the program jumps directly to the 
	job launching (delayed submission).
	*/
 
	for(i=0; i<lengthOf(ListMovies); i++){
		if (ListMovies[i] != ''){
			// Path of the movie to treat
			Path1 = ListMovies[i];

			// Open the movie
			// Depending on the extension the program use the quicktime plugin...
			if(RefExt == '.mov'){
				run('Using QuickTime...', 
					'open=[' + Path1 + '] convert'); 
			}else{
				//...or IJ opener
				open(Path1);
			}

			// Change properties to avoid issue between measure in pixels and
			// drawing in microns...
			getDimensions(width, height, channels, slices, frames);
			Stack.setXUnit('pixel');
			cmd = 'channels=' + channels + ' slices=' + slices;
			cmd += ' frames=' + frames;
			cmd += ' pixel_width=1 pixel_height=1 voxel_depth=1.0000000'; 
			run('Properties...', cmd);

			// Get movie dimension and name	
			NomImage = getTitle();
			H = getHeight();
			W = getWidth();

			// Manual drawing of the zygote
			// As the shape of the zygote is changing the program displays the
			// frame corresponding to the half of the movie.
			setSlice(round(nSlices() / 2));

			// A rectangle is automatically drawn on the movie. 
			// The user can modify it
			makeRectangle(50, 50, W-100, H-100);
			msg = 'Movie ' + i + ' out of ' + lengthOf(ListMovies);
			msg += '\n\nSelect the ZYGOTE then press OK\n\nIF YOU WANT TO STOP';
			msg += ' AND ESC THE MACRO IT IS NOW';	
			waitForUser(msg);

			// The rectangle drawn by the user is stored in the roiManager 
			roiManager("Add"); 	

			// Initialisation of the command line	
			CL = '';

			// Update the command line with the path of the movie
			CL += Path1 + '\t';	

			// Crop of the zygote in all frames
			// Get "zygote rectangle" position and dimensions
			roiManager('Select', roiManager('count') - 1);
			List.setMeasurements;
			Xz = List.getValue('BX');
			Yz = List.getValue('BY');
			Wz = List.getValue('Width');
			Hz = List.getValue('Height');
			
			// Update command line with "zygote rectangle" position 
			// and dimensions
			CL += '' + Xz + '\t' + Yz + '\t' + Wz + '\t' + Hz+ '\t';
	
			// Crop the zygote and update the dimensions of the movie
			run('Crop');
			H = getHeight();
			W = getWidth();
	
			// Height of the zygote in the reference movie (in pix)
			HRef = 518;
 			// Diameter of scanning circle for the reference movie (dsc in pix).
			DiametreCercleR = 60;	
	
			// Calculating the ratio between the actual movie dimension and 
			// the reference one
			ratio = H / HRef;
	
			/*
			Adapt research parameters to the actual dimension of the movie.
			The research algorythm will then be fitted to the movie resolution, 
			making the user's parameters only dependent on strain 
			and/or phenotype
			*/

			// Diameter of the scanning Circle (dsc)
			DiametreCercle = round(DiametreCercleR * ratio);			

			// Cut the movie using the function MontageFilm
			Parametres = MontageFilm(Reso, t);
	
			// The beginning, Origine and ending frame of the movie are 
			// retrieved from the function MontageFilm
			Sstart = Parametres[0];
			Origine = Parametres[1];
			Send= Parametres[2];
	
			// Manual positioning of the anterior centrosome on beginning frame
			setSlice(Sstart);
			// Automatic drawing of a circle. User can modify its position
			makeOval(DiametreCercle,
					 DiametreCercle,
					 DiametreCercle,
					 DiametreCercle);
			
			// To avoid the waitForUser commands to be considered as redundant 
			// and make ImageJ bugging
			wait(500);
			msg = 'Position this circle on the center\n';
			msg += 'of the anterior spindle.\nThen press OK.';
			waitForUser(msg);
			roiManager('Add');
			
			// Get position of the center of the centrosome
			roiManager('Select', roiManager('count') - 1);
			List.setMeasurements;
			Xant = List.getValue('X');
			Yant = List.getValue('Y');

			// Manual positioning of the posterior centrosome on beginning frame
			setSlice(Sstart);
			
			// Automatic drawing of a circle. User can modify its position
			makeOval(W - 2 * DiametreCercle,
					 DiametreCercle,
					 DiametreCercle,
					 DiametreCercle);
			msg = 'Position this circle on the center\n';
			msg += 'of the posterior spindle.\nThen press OK.';
			waitForUser(msg);
			
			// To avoid the waitForUser commands to be considered as redundant 
			// and make ImageJ bugging
			wait(500);
			roiManager('Add');

			// Get position of the center of the centrosome
			roiManager('Select', roiManager('count') - 1);
			List.setMeasurements;
			Xpost = List.getValue('X');
			Ypost= List.getValue('Y');

			// Manual positioning of the anterior centrosome on ending frame
			setSlice(Send);
			
			// Automatic drawing of a circle. User can modify its position
			makeOval(DiametreCercle,
					 DiametreCercle,
					 DiametreCercle,
					 DiametreCercle);
			msg = 'Position this circle on the center\n';
			msg += 'of the anterior spindle.\nThen press OK.';
			waitForUser(ms);

			// To avoid the waitForUser commands to be considered as redundant 
			// and make ImageJ bugging
			wait(500);
			roiManager('Add');
		
			// Get position of the center of the centrosome
			roiManager('Select', roiManager('count') - 1);
			List.setMeasurements;
			XantF = List.getValue('X');
			YantF = List.getValue('Y');

			// Manual positioning of the posterior centrosome on beginning frame
			// Automatic drawing of a circle. User can modify its position
			setSlice(Send);
			makeOval(W - 2 * DiametreCercle,
					 DiametreCercle,
					 DiametreCercle,
					 DiametreCercle);
			msg = 'Position this circle on the center\n';
			msg += 'of the posterior spindle.\nThen pressOK.';
			waitForUser(msg);

			// To avoid the waitForUser commands to be considered as redundant 
			// and make ImageJ bugging
			wait(500);
			roiManager('Add');

			// Get position of the center of the centrosome
			roiManager('Select', roiManager('count') - 1);
			List.setMeasurements;
			XpostF = List.getValue('X');
			YpostF= List.getValue('Y');

//Display advanced settings
	
Annul = 0;	//Set the abort movie option to false
	
	if(Mode==1){
	
	Choix=newArray("Analysis", "Abort");	//The user has the possibility to remove the movie from the analysis (problems during acquisition...)
	labels = newArray("Forward", "Retrograde");
	RefAnt = newArray(SensAnt,BoxAnt);
	RefPost = newArray(SensPost,BoxPost);
	
	//User interface fed with "generic" parameters obtained from the function "Principal"
	Dialog.create("Advanced settings");
	Dialog.addMessage("Movie analysis will be performed with these parameters\nStarting frame: "+Sstart+"\n\nReference frame: "+Origine+"\n\nEnding frame: "+ Send);
	Dialog.addMessage("Please indicate the movie resolution:");
	Dialog.addNumber("1 pixel =", Reso);
	Dialog.addSlider("Time scale (sec)", 0.25, 2, t);
	Dialog.addMessage("If the movie quality is not good you can abort and\n jump to the next movie");
	Dialog.addMessage("ANTERIOR ASTER TRACKING SETTINGS:");
	Dialog.addCheckboxGroup(1,2,labels, RefAnt);
	Dialog.addNumber("Diameter of the Scanning Circle (dsc)", DCAnt);
	Dialog.addNumber("Height of the Boundary Box (hbb)",  LPAnt);
	Dialog.addMessage("");
	Dialog.addMessage("POSTERIOR ASTER TRACKING SETTINGS:");
	Dialog.addCheckboxGroup(1,2,labels, RefPost);
	Dialog.addNumber("Diameter of the Scanning Circle (dsc)",  DCPost);
	Dialog.addNumber("Height of the Boundary Box (hbb)",  LPPost);
	Dialog.addChoice("Proceed?", Choix);
	Dialog.show();
	
	//Updating the analysis parameters for this movie
	Reso = Dialog.getNumber();
	t = Dialog.getNumber();
	Opt = Dialog.getChoice();
	SensAnt = Dialog.getCheckbox();
	BoxAnt = Dialog.getCheckbox();
	DCAnt = Dialog.getNumber();
	LPAnt = Dialog.getNumber();
	SensPost = Dialog.getCheckbox();
	BoxPost = Dialog.getCheckbox();
	DCPost = Dialog.getNumber();
	LPPost = Dialog.getNumber();
	
	if(Opt=="Abort"){
	Annul = 1;
	}
	
	
}

//Close the movie
	close();

//Close the roiManager to eliminate all remaining ROI
	selectWindow("ROI Manager");
	run("Close");	

//If the user confirms the incorporation of this movie in the batch analysis the command line is created and saved. Else it is skipped.
if (Annul==0){
	
//Updating the command line with all remaining parameters
CL = ""+CL + Reso + "\t"+ Sstart+"\t"+ Origine+ "\t"+ Send+ "\t"+ t;	
CL = ""	+CL
		+ "\t"+ Xant 
		+ "\t"+ Yant
		+ "\t"+ XantF
		+ "\t"+ YantF
		+ "\t"+ SensAnt
		+ "\t"+ BoxAnt
		+ "\t"+ DCAnt
		+ "\t"+ LPAnt
		+ "\t"+ Xpost 
		+ "\t"+ Ypost
		+ "\t"+ XpostF
		+ "\t"+ YpostF
		+ "\t"+ SensPost
		+ "\t"+ BoxPost
		+ "\t"+ DCPost
		+ "\t"+ LPPost
		+ "\t"+ RefExt;	
//Adding the command line to the "ListCommands.txt" file		
File.append(CL,dir+"ListCommands.txt");


}
}
}	

//END OF THE LOOP FOR MOVIE PRE-TREATMENT



//Launching of the analysis.

//Dialog window to warn the user that the job will be submitted.
Dialog.create("End of the pre-Analysis process");
Dialog.addMessage("The program will now analyse all the movies with\nthe parameters you entered.");
Dialog.show();

//Get all generated command lines
Listing=split(File.openAsString(dir+"ListCommands.txt"),"\n"); 


//LOOP FOR JOB SUBMISSION

	for(z=1; z<lengthOf(Listing);z++){
	Arguments = Listing[z];
	
	if(Listing[z] != ""){
		runMacro(getDirectory("macros")+File.separator()+"ACT_Batch"+File.separator()+"ACT_Motor_CommandLine.java", Arguments);
	}
	}

//END OF LOOP FOR JOB SUBMISSION 	

//Dialog window indicating the end of all analysis.
//The user can choose to  display the output (needs sufficient memory) or just exit.
ListeOption= newArray("Display results", "Exit program");	
Dialog.create("End of the Analysis process");
Dialog.addMessage("The program has analysed all your movies.");
Dialog.addMessage("Indicate if you want the program to dislay ALL results movies\nor exit the program.");
Dialog.addMessage("WARNING: Displaying all movies can require too much memory!");
Dialog.setInsets(0, 25, 20);
Dialog.addChoice("Option:", ListeOption);
Dialog.show();	
option = Dialog.getChoice();	

if(option == "Exit program"){
	exit();
}

//Display of all output movies
if(option == "Display results"){

DejaFait = File.openAsString(dir+"ListCommands.txt");
ListingFait = split(DejaFait,"\n");

//setBatchMode(false);
	for(i=0; i<lengthOf(ListingFait); i++){
		if(ListingFait[i] !=""){
				Decomposition = split(ListingFait[i], "\t");
				Path1 = Decomposition[0];
		
				open(Path1+ "_ASTERS.tif");
				
		}
	}
}	
//End of display of output movies

//FUNCTION ListFiles
 function listFiles(dir,ext) {
     list = getFileList(dir);							//Get the list of all files and folder present in the current folder
     for (i=0; i<list.length; i++) {
        if (endsWith(list[i], ext)&&(endsWith(list[i],"_ASTERS.tif")==0)){	//In case of tif movies, a filters will remove all previous ouptput movies (characterized with "_ASTERS.tif" suffix)
        print(dir+list[i]);							//Add the file with the correct extension to the list
        }
        if (endsWith(list[i], "/")){						//If the path correspond to a subfolder, it is analyzed.
        listFiles(""+dir+list[i], ext);
        }         
     }
  }


//FUNCTION MontageFilm  
  function MontageFilm(Reso, t){
	
	//Manual indication of the Beginning frame
	waitForUser("Indicate FIRST frame of the movie\nand click OK!");
	Sstart = getSliceNumber();
	
	wait(500);	//To avoid the waitForUser commands to be considered as redundant and make ImageJ bugging
	
	//Manual indication of the Origine frame
	waitForUser("Indicate REFERENCE frame of the movie\nand click OK!");
	Origin = getSliceNumber();
	wait(500);
	
	//Manual indication of the Ending frame
	waitForUser("Indicate LAST frame of the movie\nand click OK!");
	Send = getSliceNumber();
	wait(500);	//To avoid the waitForUser commands to be considered as redundant and make ImageJ bugging

	Resultat = newArray(3);	//Create an array that will contain the position of the refernce frames
	Resultat[0]= Sstart;	
	Resultat[1]= Origin;
	Resultat[2] = Send;
	
	//Return the numbers of the frames to the main program
	return Resultat;
}


//FUNCTION Principal 
function Principal(Version, RefExtIn, Mode, Reso, t, SensAnt, BoxAnt, DCAnt, LFAnt, LPAnt, SensPost, BoxPost, DCPost, LFPost, LPPost){
	
	//Creation of the option lists
	Liste = newArray(RefExtIn, ".mov",".tif", ".TIF", ".TIFF",".stk");
	labels = newArray("Forward", "Retrograde");
	RefAnt = newArray(SensAnt,BoxAnt);
	RefPost = newArray(SensPost,BoxPost);
	Actions = newArray("Perform movie analyses", "Save settings");
	//Save settings is for now not functional
	
	//Dialog window
	Dialog.create("");
	Dialog.addMessage("Welcome to A.C.T. version: "+Version);
	Dialog.addMessage("ACTUAL SETTINGS");
	Dialog.addChoice("Movie extension: ", Liste);
	Dialog.addCheckbox("Advanced pretreatment mode ", Mode);
	Dialog.addNumber("Default resolution: " , Reso, 3, 5, "um/pix");
	Dialog.addNumber("Default time interval: " , t, 3, 5, "sec");
	Dialog.addMessage("ANTERIOR CENTROSOME TRACKING ENGINE:");
	Dialog.addCheckboxGroup(1,2,labels, RefAnt);
	Dialog.addNumber("Diameter of the Scanning Circle (dsc)",  DCAnt);
	Dialog.addNumber("Height of the Boundary Box (hbb)",  LPAnt);
	Dialog.addMessage("POSTERIOR CENTROSOME TRACKING ENGINE:");
	Dialog.addCheckboxGroup(1,2,labels, RefPost);
	Dialog.addNumber("Diameter of the Scanning Circle (dsc)",  DCPost);
	Dialog.addNumber("Height of the Boundary Box (hbb)", LPPost);
	Dialog.addMessage("");
	//Dialog.addChoice("", Actions);
	Dialog.show();
	
	//Choix = Dialog.getChoice();
	
	RefExt = Dialog.getChoice();
	Mode = Dialog.getCheckbox();
	Reso = Dialog.getNumber();
	t = Dialog.getNumber();
	SensAnt = Dialog.getCheckbox();
	BoxAnt = Dialog.getCheckbox();
	DCAnt = Dialog.getNumber();
	LPAnt = Dialog.getNumber();
	SensPost = Dialog.getCheckbox();
	BoxPost = Dialog.getCheckbox();
	DCPost = Dialog.getNumber();
	LPPost = Dialog.getNumber();
	
	//Return the "generic" parameters to the main program.
	R = RefExt+"\t"+Mode+"\t"+Reso+"\t"+t+"\t"+SensAnt+"\t"+BoxAnt+"\t"+DCAnt+"\t"+LPAnt+"\t"+SensPost+"\t"+BoxPost+"\t"+DCPost+"\t"+LPPost;
	return R;
	
} 
  
}
