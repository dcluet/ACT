Automated Tracking of Centrosomes
===

Authors
--
* **CLUET David**:     david.cluet@ens-lyon.fr
* **SPICHTY Martin**:  spichty.martin@ens-lyon.fr
* **DELATTRE Marie**:  marie.delattre@ens-lyon.fr

License
--

Copyright CNRS 2013


>This software is a computer program whose purpose is to automatically track
centrosomes in DIC movies.
>
>This software is governed by the CeCILL  license under French law and abiding
by the rules of distribution of free software. You can use, modify and/ or
redistribute the software under the terms of the CeCILL license as circulated
by CEA, CNRS and INRIA at the following URL:
http://www.cecill.info/index.en.html
>
>As a counterpart to the access to the source code and  rights to copy, modify
and redistribute granted by the license, users are provided only with a limited
warranty  and the software's author,the holder of the economic rights, and the
successive licensors have only limited liability.
>
>In this respect, the user's attention is drawn to the risks associated with
loading, using, modifying and/or developing or reACT.jpgproducing the software by the
user in light of its specific status of free software, that may mean  that it
is complicated to manipulate, and that also therefore means  that it is
reserved for developers  and  experienced professionals having in-depth
computer knowledge. Users are therefore encouraged to load and test the
software's suitability as regards their requirements in conditions enabling
the security of their systems and/or data to be ensured and, more generally,
to use and operate it in the same conditions as regards security.
>
>The fact that you are presently reading this means that you have had knowledge
of the CeCILL license and that you accept its terms.

Requirements
--
* The **ACT** macro requires `ImageJ v1.47s` or higher: https://imagej.nih.gov/ij/download.html
* The `.mov` files require the `Quicktime plugin` for ImageJ: https://imagej.nih.gov/ij/plugins/movie-opener.html

Files
--
- [] **ACT**
    - README.md
    - LICENSE.txt
    - [] **doc**
        - *ACT_User_Guide_CeCILL_2014-02-28.pdf*
        - *journal.pone.0093718.PDF*
    - [] **src**
        - `Installation.ijm`
        - [] **Macro**
            - `ACT_Motor_CommandLine.ijm`
            - `ACT_Table_CommandLine_creation.ijm`
            - `ACT.jpg`
            - `CMD_SUM.ijm`

Installation procedure
--
By default your `ImageJ` program has no shortcut to the **ACT macro**. The objective of this installation procedure is to transfer automatically the **ACT macro** and all its required files within `ImageJ/Macros` subfolder.
 Finally, for convenient usage, shortcuts will be automatically generated within
the Plugins/Macros menu. If you have already a version of **ACT**, the installation procedure will overwrite it, making easy updating of your system.

1. First launch the `ImageJ` program.
2. Open the folder you downloaded from our web site. It contains the current
version of the **ACT** macro and the `Installation.ijm` file. Drag this file and drop it on `ImageJ` command bar. A new window will automatically pop up.
3. Use in the menu bar the `Macros/Run Macro` command.
4. The previous window disappears and `License agreement` window of the installation program is displayed. `Check` the license agreement box. `Click` on `OK` to proceed with the installation.
5. The installation is then performed in few seconds. At the end of the process the program inform you of the success of the installation.
6. Quit `ImageJ` to update the `startup macro file`  (If you perform an updating of ACT, you can already use it without restarting
ImageJ).
7. Once `ImageJ` is restarted you can see two new shortcuts in the `Plugin/Macros`
menu:
    - **A.C.T.**
    - **A.C.T. Command-Lines LAUNCHER**

For further informations concerning the program please refer to the *ACT_User_Guide_CeCILL_2014-02-28.pdf* file.
