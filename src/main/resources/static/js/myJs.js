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
        $('#btnSubmit').on("click", function (e) {
            let checked = $(':checkbox:checked').length;
            if (checked == 0) {
                swal("Oops", "At least One Lab Test Should Be Selected!", "error");
                e.preventDefault();
            }
        });
    });
//prevent checkbox==null before submit - end

    //WYSIWYG add to text area
    //let textarea =document.getElementById("description");
    // sceditor.create(textarea);
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



/*
//FUNCTION INITIALIZE TO RELEVANT PART
function initialize() {
    $(cmbMedicalPackage).ready("change", cmbMedicalPackageDetailGet);
}*/
