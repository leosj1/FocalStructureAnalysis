/**
 * JS page designed to work with alternate Servlet architecture fsa_index.jsp
 * @author Andrew Pyle axpyle@ualr.edu MS-Information Science 2018
 */

$(document).ready(function() {
    
    // FIXME Include proper user guidance for CSV format
    var csvFormatIcon = document.getElementById("csvFormatIcon");
    
    csvFormatIcon.addEventListener("click", function(event) {
        alert("CSV format must be as follows:\n" +
              "node1,node2[,weight]\n\n" +
              "Example:\n" +
              "apple,orange,2\n" +
              "apple,pear,3\n" +
              "orange2,pear4,4");
    });
    
  /**
   * Upload CSV asynchronously
   * One CSV file accepted
   */ 
  var form = document.getElementById("uploadForm");
  var filePicker = document.getElementById("filePicker");
  var submit = document.getElementById("uploadSubmit");
  var progressBar = document.getElementById("uploadProgress");
  var progressLabel = document.getElementById("uploadProgressLabel");
  var formLabel = document.getElementById("uploadFormLabel");
  
  // Defaults
  submit.disabled = true;
  
  // Parse file locally
  $("#filePicker").change(function(event) {
    
    // Defaults
    progressBar.value = 0;
    progressLabel.innerHTML = "";
    submit.disabled = true;
    
    var csvFile = event.target.files[0];  // Only accept one CSV file
    // console.log(csvFile); // Debug
    
    // Disables submit button & clears file upload if invalid file
    validateCSVFormat(csvFile);
  });
  
  
  form.addEventListener("submit", (function(event) {
    
    // Prevent HTML form POST
    event.preventDefault();
    
    var formData = new FormData(event.target);
    var xhr = new XMLHttpRequest();
    
    // Only 1 file upload allowed on HTML form
    if (event.target["filePicker"].files.length != 0) {
      
      
      // for(var pair of formData.entries()) {
      //   console.log(pair[0]+ ', '+ pair[1]);
      // }

      xhr.upload.addEventListener("progress", function(event) {
        
        if (event.lengthComputable) {
          // Update html progress bar: 0.1 - 1.0
          decimalComplete = event.loaded / event.total;
          progressBar.value = decimalComplete;  // TODO round up to 100
          var progressDescription = event.loaded + " / " + event.total + " | " + decimalComplete;
          
          // console.log(progressDescription)  // Debug
          progressLabel.innerHTML = progressDescription;
          
          if (progressBar.value > 0.99999) {
            progressBar.removeAttribute("value");
            progressLabel.innerHTML = "Processing...";
            // console.log("Inserting CSV rows");  // Debug
          }
          
        } else {
          console.log("length unknown");
        }
      });

      xhr.onreadystatechange = function() {

        if(xhr.readyState === 4 && xhr.status === 200) {
          
          // Make Progress Bar Complete
          progressBar.value = 1;
          progressLabel.innerHTML = "Complete! Reload Page to select uploaded network";
          
          console.log("xhr complete!");
          console.log(xhr.responseText);
          
          // TODO async populate dropdown rather than reload page
          window.location.reload(true);  // force reload
          
        } else if (xhr.readyState === 4 && xhr.status === 500) {
          
        	// Reset Progress Bar & Message
          progressBar.value = 0;
          progressLabel.innerHTML = "";
          
          // Alert User of Failure
          console.log(xhr.statusText);
          alert("Error Uploading CSV");
          
        }
      };
      
      xhr.open("POST", "upload-csv", true) // TODO get location from HTML
      xhr.send(formData);

      } else {
        alert("No file selected!");
      }
  }), false);
  
  
  function validateCSVFormat(csvFile) {
    var reader = new FileReader();
    
    // Validate inside callback after reading file to string to 
    // preserve synchronicity
    reader.onload = function(event) {
        
        // Get string representation of file and clean whitespace
        var csvText = event.target.result;
        var csvTrim = csvText.trim();  // remove blank lines at beginning & end
        csvTrim += "\n";  // restore last linebreak removed by .trim()
                
        /* FSA CSV Format Regex Explanation
         *
         * ^             start of string
         * (?:           atomic group
         *   [\w\s-\.@]+  1+ alphanumeric, underscore, hyphen, period, or "@"
         *   ,           comma literal
         *   [\w\s-\.@]+  1+ alphanumeric, underscore, hyphen, period, or "@"
         *   (?:         atomic group
         *     ,         comma literal
         *     (?:       atomic group
         *       \d*     0+ digit
         *       \.      decimal point literal
         *     )?        end of group. 0-1 of group
         *     \d+       1+ digit
         *   )?          end of group. 0-1 of group
         *   \r?         0-1 carriage return
         *   \n          line feed literal
         * )+            end of group. 1+ of group
         * $             end of string
         * 
         * Node names can have alphanumeric, underscore, hyphen or period
         * Weights can be 0.1 or .1 or 1 but not 1.
         */
        
        var csvRegex = /^(?:[\w\s-\.@]+,[\w\s-\.@]+(?:,(?:\d*\.)?\d+)?\r?\n)+$/g;
        
        
        var name = csvFile.name;
        var type = csvFile.type;
        var size = csvFile.size;
        var lastModified = csvFile.lastModified;
        
        // Validate File Size
        if (size > 100000) {  // 100 kB
            alert("We are unable to process files " +
                  "larger than 100 kB at this time.\n" +
                  "We apologize for the inconvenience.");
            
            stopFileAttachment();
            return;
        }
        
        // Validate MIME Type
        else if (type != "text/csv") {  // CSV file only
            alert("We only accept CSV files at this time.\n" +
                  "We apologize for the inconvenience.");
            
            stopFileAttachment();
            return;
        }
        
//        else if (!csvRegex.test(csvTrim)) {  // FSA CSV format only
//            alert("Incorrect format. " +
//                  "CSV format must be as follows:\n" +
//                  "node1,node2[,weight]\n\n" +
//                  "Example:\n" +
//                  "apple,orange,2\n" +
//                  "apple,pear,3\n" +
//                  "orange2,pear4,4");
//            // console.log("regex test: ", csvRegex.test(csvTrim)); // Debug
//            
//            stopFileAttachment();
//            return;
//        }
        
        else {  // Else, CSV passed all tests
            submit.disabled = false;
            // console.log("CSV passed all tests");  // Debug
        }

        // console.log("type of object:", typeof event.target.result); // Debug
        // console.log(countLines(event.target.result), "lines"); // Debug
    };
    
    reader.readAsText(csvFile);
  }
  
  function stopFileAttachment() {
      submit.disabled = true;
      filePicker.value = null;
  }
  
  //Function based on StackOverflow answer 
  //https://stackoverflow.com/questions/45630235/how-to-get-the-number-of-lines-in-a-text-file-without-opening-it
  // (C) CC BY-SA 3.0 Michael Geary - https://stackoverflow.com/users/1202830/michael-geary 
  function countLines(csv) {
    var nLines = 0;
    csvTrim = csv.trim();  // remove blank lines at beginning & end
    csvTrim = csvTrim + "\n";  // restore last linebreak removed by .trim()
    for( var i = 0, n = csvTrim.length; i < n; ++i) {
      if (csvTrim[i] === '\n') {
        ++nLines;
      }
    }
    return nLines;
  }
});