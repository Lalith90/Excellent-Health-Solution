$(document).ready(function () {
    // set current year to the footer
    document.getElementById("currentYear").innerHTML = new Date().getFullYear();

    /*//Nav bar properties - start//*/
    $('ul.nav li.dropdown').hover(function () {
        $(this).find('.dropdown-menu').stop(true, true).delay(200).fadeIn(10);
    }, function () {
        $(this).find('.dropdown-menu').stop(true, true).delay(200).fadeOut(10);
    });
    /*//Nav bar properties - end//*/

    /* selected field colour and add selected lab test table - start*/
    $('#myTable tbody tr').bind('click', function (e) {
        $(e.currentTarget).children('th').css('background-color', '#00FFFF');
        checkLabTestInArrayOrNot($(e.currentTarget).children('th'));
    });
    /* selected field colour and add selected lab test table - end*/
    /*//--------------- data table short using - data table plugin ------- start //*/
    $("#myTable").DataTable({
        "lengthMenu": [[5, 10, 15, 20, -1], [5, 10, 15, 20, "All"]],
        "ordering": false,
        stateSave: true,
    });
    /*//--------------- data table short using - data table plugin ------- start //*/

    /* Patient and employee Nic Validation - start*/
    $("#nic").bind('keyup', function () {
        let nic = $(this).val();
        $("#dateOfBirth").val(calculateDateOfBirth(nic));
        $("#gender").val(calculateGender(nic));
    });
    /* Patient and employee Nic Validation - end*/

//prevent checkbox==null before submit -start
    $(function () {
        $('#btnSubmit').click(function (e) {
            var checked = $(':checkbox:checked').length;
            if (checked == 0) {
                alert('Atleast One Lab Test Should Be Selected');
                e.preventDefault();
            }
        });
    });
//prevent checkbox==null before submit - end

    //WYSIWYG add to text area
    let textarea =document.getElementById("description");
    sceditor.create(textarea);
    //     , {
    //     format: 'bbcode',
    //         icons: 'monocons',
    //         style: '../minified/themes/content/default.min.css'
    // });

});

                /*//Nic - data of birth - start//*/
function dateLengthValidate(day) {
    if (day.toLocaleString().length === 1) {
        return day = '0' + day;
    }
    return day;
}

function calculateDateOfBirth(nic) {

    let dateOfBirth = null;
    let day = null;
    let month;
    if (nic.length === 10) {
        day = +nic.substr(2, 3);
        dateOfBirth = '19' + nic.substr(0, 2) + '-';
        if (day > 500) day = day - 500;

        //<editor-fold desc="if else block">
        if (day > 335) {
            day = day - 335;
            day = day = dateLengthValidate(day);
            month = 12;
        } else if (day > 305) {
            day = day - 305;
            day = dateLengthValidate(day);
            month = 11;
        } else if (day > 274) {
            day = day - 274;
            day = dateLengthValidate(day);
            month = 10;
        } else if (day > 244) {
            day = day - 244;
            day = dateLengthValidate(day);
            month = 9;
        } else if (day > 213) {
            day = day - 213;
            day = dateLengthValidate(day);
            month = 8;
        } else if (day > 182) {
            day = day - 182;
            day = dateLengthValidate(day);
            month = 7;
        } else if (day > 152) {
            day = day - 152;
            day = dateLengthValidate(day);
            month = 6;
        } else if (day > 121) {
            day = day - 121;
            day = dateLengthValidate(day);
            month = 5;
        } else if (day > 91) {
            day = day - 91;
            day = dateLengthValidate(day);
            month = 4;
        } else if (day > 60) {
            day = day - 60;
            day = dateLengthValidate(day);
            month = 3;
        } else if (day < 32) {
            day = day;
            day = dateLengthValidate(day);
            month = 1;
        } else if (day > 31) {
            day = day - 31;
            day = dateLengthValidate(day);
            month = 2;
        }
        //</editor-fold>
        if (month.toLocaleString().length === 2) {

            dateOfBirth = dateOfBirth + month + '-' + day;
        } else {

            dateOfBirth = dateOfBirth + '0' + month + '-' + day;
        }


    } else if (nic.length === 12) {
        dateOfBirth = nic.substr(0, 4) + '-';
        day = +nic.substr(4, 3);
        if (day > 500) day = day - 500;
        //<editor-fold desc="if else block">
        if (day > 335) {
            day = day - 335;
            day = dateLengthValidate(day);
            month = 12;
        } else if (day > 305) {
            day = day - 305;
            day = dateLengthValidate(day);
            month = 11;
        } else if (day > 274) {
            day = day - 274;
            day = dateLengthValidate(day);
            month = 10;
        } else if (day > 244) {
            day = day - 244;
            day = dateLengthValidate(day);
            month = 9;
        } else if (day > 213) {
            day = day - 213;
            day = dateLengthValidate(day);
            month = 8;
        } else if (day > 182) {
            day = day - 182;
            day = dateLengthValidate(day);
            month = 7;
        } else if (day > 152) {
            day = day - 152;
            day = dateLengthValidate(day);
            month = 6;
        } else if (day > 121) {
            day = day - 121;
            day = dateLengthValidate(day);
            month = 5;
        } else if (day > 91) {
            day = day - 91;
            day = dateLengthValidate(day);
            month = 4;
        } else if (day > 60) {
            day = day - 60;
            day = dateLengthValidate(day);
            month = 3;
        } else if (day < 32) {
            day = day;
            day = dateLengthValidate(day);
            month = 1;
        } else if (day > 31) {
            day = day - 31;
            day = dateLengthValidate(day);
            month = 2;
        }
        //</editor-fold>
        if (month.toLocaleString().length === 2) {
            dateOfBirth = dateOfBirth + month + '-' + day;
        } else {
            dateOfBirth = dateOfBirth + '0' + month + '-' + day;
        }
    }
    return dateOfBirth;
}

                /*//Nic - data of birth - end//*/

                /*//Nic - gender - start//*/
function calculateGender(nic) {
    let gender = null;
    if (nic.length === 10 && nic[9] === "V" || nic[9] === "v" || nic[9] === "x" || nic[9] === "X") {
        if (nic[9] === "v" || nic[9] === "x") {
            alert(` Please change "v" or "x" to "V" or "X" `);
        }
        if (+nic.substr(2, 3) < 500) gender = 'MALE';
        else gender = 'FEMALE';

    } else if (nic.length === 12) {
        if (+nic.substr(4, 3) < 500) gender = 'MALE';
        else gender = 'FEMALE';
    }
    return gender;
}

                    /*//Nic - gender - end//*/
/*----------------------------------------------------------------Invoice creation ----> start----------------------------------------------------------------*/


/*// Create new table selected lab test table - start//*/
class LabTest {
    constructor(id, code, name, price) {
        this._id = id;
        this._code = code;
        this._name = name;
        this._price = price;
    }

    get id() {
        return this._id;
    }

    set id(value) {
        this._id = value;
    }

    get code() {
        return this._code;
    }

    set code(value) {
        this._code = value;
    }

    get name() {
        return this._name;
    }

    set name(value) {
        this._name = value;
    }

    get price() {
        return this._price;
    }

    set price(value) {
        this._price = value;
    }
}

function rowDataToLabTest(rowDetails) {
    const labTest = new LabTest();
    for (let i = 0; i <= rowDetails.length; i++) {
        switch (i) {
            case 0:
                labTest.id = rowDetails[i].innerHTML;
                break;
            case 1:
                labTest.code = rowDetails[i].innerHTML;
                break;
            case 2:
                labTest.name = rowDetails[i].innerHTML;
                break;
            case 3:
                labTest.price = rowDetails[i].innerHTML;
                break;
            default:
                return labTest;
        }
    }
}

//SELECTED LAB TEST
let selectedLabTestArray = [];
//SELECTED MEDICAL PACKAGE
let selectedMedicalPackageId;
//SELECTED LAB TEST TOTAL PRICE
let totalLabTestPrice;

function checkLabTestInArrayOrNot(rowDetails) {
    let existOrNot;
    //take lab test which was selected
    let labTest = rowDataToLabTest(rowDetails);
    // no lab test in Array
    if (selectedLabTestArray.length === 0) {
        selectedLabTestArray.push(labTest);
        addRow(labTest);
    } else {
        for (let i = 0; i < selectedLabTestArray.length; i++) {
            if (selectedLabTestArray[i]._id === labTest.id) {
                existOrNot = true;
                break;
            }
        }
        if (existOrNot) {
            swal({
                title: "Already selected one ",
                icon: "warning",
            });
        } else {
            selectedLabTestArray.push(labTest);
            addRow(labTest);
        }
    }
}

function addRow(labTest) {
    let table = document.getElementById("myTableData");
    let rowCount = table.rows.length;

    let row = table.insertRow(rowCount);

    updateTotalPrice(labTest.price);

    row.insertCell(0).innerHTML = labTest.id;
    row.insertCell(1).innerHTML = labTest.code;
    row.insertCell(2).innerHTML = labTest.name;
    row.insertCell(3).innerHTML = labTest.price;

    row.insertCell(4).innerHTML = '<input type="button" value = "Remove" onClick="deleteRow(this)" class="btn btn-danger">';

}

function deleteRow(obj) {
    let index = obj.parentNode.parentNode.rowIndex;
    let table = document.getElementById("myTableData");
    // GET THE CELLS COLLECTION OF THE CURRENT ROW.
    let objCells = myTableData.rows.item(index).cells;
    //REMOVE DELETED LAB TEST FORM selectedLabTestArray
    let removedLabTest;
    for (let i = 0; i < selectedLabTestArray.length; i++) {
        if (selectedLabTestArray[i]._id === rowDataToLabTest(Object.values(objCells)).id) {
            removedLabTest = selectedLabTestArray[i];
            {
                selectedLabTestArray.splice(i, 1);
                break;
            }
        }
    }
    // remove colour form original lab test array
    let mainTable = document.getElementById("myTable");
    for (let i = 0; i < mainTable.rows.length; i++) {
        let removedLabTestFromArray = rowDataToLabTest(mainTable.rows.item(i).cells);
        console.log(removedLabTestFromArray);
        if (removedLabTestFromArray.id === removedLabTest._id) {
            mainTable.rows[i].setAttribute("class", "removeLabTest");
        }
    }
    updateTotalPrice();
    table.deleteRow(index);
}

function updateTotalPrice() {
    totalLabTestPrice = 0;
    selectedLabTestArray.forEach(function (element) {
        totalLabTestPrice += parseFloat(element.price);
    });
    document.getElementById("selectedLabTestCount").innerText = "Selected Lab Test Count = " + selectedLabTestArray.length;
    document.getElementById("totalPrice1").innerText = "Total Price = " + totalLabTestPrice;
    document.getElementById("totalPrice").value = totalLabTestPrice;

}

/*// Create new table selected lab test table - end//*/

        /*//medical package details show and manage - start //*/
$('#cmbMedicalPackage').on("change",function cmbMedicalPackageDetailGet() {
    // get current url
    let currentURL = window.location.href;
    // get selected medical package id
    selectedMedicalPackageId = document.getElementById('cmbMedicalPackage').value;
    // ajax response
    let givenData = getData(`${currentURL}/medicalPackageLabTestGet/${selectedMedicalPackageId}`).then(data => givenData = data);

    Promise.resolve(givenData).then(function (val) {
        medicalPackageLabTestSet(val);
    });
});

        //fill the medical package class details
function medicalPackageLabTestSet(includeLabTest) {
    removeMedicalPackageDetail();
    document.getElementById("includedLabTestCount").innerHTML = includeLabTest.length;
    for (let i = 0; i < includeLabTest.length; i++) {
        fillMedicalPackageDetail(includeLabTest[i]);
    }
}

        //access and fill medical package details table
function fillMedicalPackageDetail(medicalPackageLabTest) {
    let table = document.getElementById("myMedicalPackageData");
    let rowCount = table.rows.length;
    let row = table.insertRow(rowCount);

    row.insertCell(0).innerHTML = medicalPackageLabTest.code;
    row.insertCell(1).innerHTML = medicalPackageLabTest.name;
    row.insertCell(2).innerHTML = medicalPackageLabTest.labtestDoneHere;
}

function removeMedicalPackageDetail() {
    let table = document.getElementById("myMedicalPackageData");
    let rowCount = table.rows.length;
    for (let x = rowCount - 1; x > 0; x--) {
        table.deleteRow(x);
    }
}

        /*//medical package details show and manage - end //*/

/*//-----------------> Information selection ------ start <----------------------------//*/





/*//-----------------> Information selection ------ end <----------------------------//*/
//AJAX FUNCTION CALL
async function getData(url) {
    try {
        const result = await fetch(url);
        return await result.json();
    } catch (e) {
        console.log(JSON.parse(e));
    }

}
/*----------------------------------------------------------------Invoice creation ----> end----------------------------------------------------------------*/

/*
//FUNCTION INITIALIZE TO RELEVANT PART
function initialize() {
    $(cmbMedicalPackage).ready("change", cmbMedicalPackageDetailGet);
}*/
