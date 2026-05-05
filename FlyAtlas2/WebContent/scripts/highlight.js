/* highlight.js 04.07.2016 — Functions for highlighting cells in results tables */


	function hiliteGene(theCell) 
    {
    	var  geneTableID = getParentTableID(theCell);
  		if(theCell.className=="outlineG")
  		{
  			clearBordersG();  			     	
  			clearBordersT();
        }
        else
        {
          	clearBordersG();
        	theCell.classList.add("outlineG");
        	hiliteTrans(theCell.id,  geneTableID);    	 
        }
    }
        
    // Take id of clicked cell and targets second table
    function hiliteTrans(geneCellID, geneTableID)
    {  
        clearBordersT();
        // IDs follow underscore
		var transTableID = "tabTrans" + geneTableID.substring(geneTableID.indexOf("_"));
		geneCellID = geneCellID.substring(geneCellID.indexOf("_") + 1);
    	var transTable = document.getElementById(transTableID); 
    	for (var i=1; i<transTable.rows.length; i++) // modified for two name rows
		{
			for (var j=2; j < transTable.rows[i].cells.length; j++)
			{
				if(j-1==geneCellID)	// modified for two name cells
				{
					if(i==1)
					{
						transTable.rows[i].cells[j].classList.add("highlightT");
					}
					else
					{
						transTable.rows[i].cells[j].classList.add("outlineT");
					}
				}
			}
		}  
    }

    // Clears borders from Gene tables (e.g. before making border change)    
	function clearBordersG()
    {
    	var tablesG = document.getElementsByClassName("geneR");
    	for (var i = 0; i < tablesG.length; i++)
    	{
    		var cells = tablesG[i].getElementsByTagName("td");
    		for (var j = 0; j < cells.length; j++) 
			{ 
				cells[j].classList.remove("outlineG");
			}
    	}
    }
    
    // Clears borders and highlight from Transcript tables (e.g. before making change)
    function clearBordersT()
    {
        var tablesT = document.getElementsByClassName("transcriptR");
    	for (var i = 0; i < tablesT.length; i++)
    	{
    		var cells = tablesT[i].getElementsByTagName("td"); 
			for (var j = 0; j < cells.length; j++) 
			{ 
				cells[j].classList.remove("highlightT");
				cells[j].classList.remove("outlineT");
			}    		
    	}
    }
      
    // The "=" in the while loop continually assigns a parent node to the variable el
    // until the node name of the element is "TABLE", when it can access its ID.
    function getParentTableID(el)
    {
    	    while ((el = el.parentElement) && el.nodeName.toUpperCase() !== "TABLE");
    	    {
    			return el.id;
    		}
    }