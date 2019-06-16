let intRegex = /^[0-9]+$/;

//full blood count - start
$("#44").on("keyup", function () {
    if (intRegex.test($(this).val())) {
        backgroundColourChangeGood($(this));
        wbcChallenge($("#45, #46, #47, #48, #49"));
    } else {
        if ($(this).val() === "") {
            backgroundColourChangeNothingToChange($(this));
            $("#45, #46, #47, #48, #49").val("");
            backgroundColourChangeNothingToChange($("#45, #46, #47, #48, #49"));
        } else {
            backgroundColourChangeBad($(this));
        }
    }
});

$("#45, #46, #47, #48, #49").on("keyup", function () {
    wbcChallenge($(this));
});

function wbcChallenge() {
//      W.B.C ( id="44")
    let wBC = $("#44").val();

//      NEUTROPHILS( id="45",id="FBC3")
//      LYMPHOCYTES ( id="46",id="FBC4")
//      EOSINOPHIL( id="47",id="FBC5")
//      MONOCYTES (  id="48",id="FBC6")
//      BASOPHILS ( id="49",id="FBC7")

    //      NEUTROPHILS( id="45",id="FBC3")
    let neutroAbso;
    //      LYMPHOCYTES ( id="46",id="FBC4")
    let liphoAbso;
    //      EOSINOPHIL( id="47",id="FBC5")
    let eosiAbso;
    //      MONOCYTES (  id="48",id="FBC6")
    let monoAbso;
    //      BASOPHILS ( id="49",id="FBC7")
    let basoAbso;

    if ($("#45").val() !== "") {
        neutroAbso = $("#FBC3").val(($("#45").val() * wBC) / 100).val();
    } else {
        backgroundColourChangeNothingToChange($("#45"));
    }
    if ($("#46").val() !== "") {
        liphoAbso = $("#FBC4").val(($("#46").val() * wBC) / 100).val();
    } else {
        backgroundColourChangeNothingToChange($("#46"));
    }
    if ($("#47").val() !== "") {
        eosiAbso = $("#FBC5").val(($("#47").val() * wBC) / 100).val();
    } else {
        backgroundColourChangeNothingToChange($("#47"));
    }

    if ($("#48").val() !== "") {
        monoAbso = $("#FBC6").val(($("#48").val() * wBC) / 100).val();
    } else {
        backgroundColourChangeNothingToChange($("#48"));
    }

    if ($("#49").val() !== "") {
        basoAbso = $("#FBC7").val(($("#49").val() * wBC) / 100).val();
    } else {
        backgroundColourChangeNothingToChange($(this));
    }

    let totalAbsolute = parseFloat(neutroAbso) + parseFloat(liphoAbso) + parseFloat(eosiAbso) + parseFloat(monoAbso) + parseFloat(basoAbso);
    if (totalAbsolute === parseFloat(wBC)) {
        backgroundColourChangeGood($("#45, #46, #47, #48, #49"));
    } else {
        if ($("#49").val() !== "" && $("#48").val() !== "" && $("#47").val() !== "" && $("#46").val() !== "" && $("#45").val() !== "") {
            backgroundColourChangeBad($("#45, #46, #47, #48, #49"));
        }
    }

}

//full blood count - end

//lipid profile - start
//           1 Cholesterol, Total, Serum (id="62")
//           2 Triglycerides (id="63")
//           3 Cholesterol, HDL, Serum (id="64")
//           4 Cholesterol, LDL, Serum (id="65")
//           5 VLDL Cholesterol (id="66")
//           6 Non-HDL Cholesterol (id="67")
//           7 Total/HDL Cholesterol ratio (id="68")
// 2 (63)/5(normal number) = 5 (66);
// 1 (62)- 3 (64) = 6 (67)
// 6 (67) - 5 (66)= 4 (65)
// 1 (62)/3 (64) = 7 (68)
$("#62, #63, #64, #65, #66, #67, #68").on("keyup", function () {
    let vddlCholesterol;
    let nonHDL;
    let cholesterolLDL;
    let totalHDLRatio;

    if ($("#63").val() !== "") {
        vddlCholesterol = $("#66").val($("#63").val() / 5).val();
    }
    if ($("#62").val() !== "" && $("#64").val() !== "") {
        nonHDL = $("#67").val($("#62").val() - $("#64").val()).val();
    }
    if ($("#67").val() !== "" && $("#66").val() !== "") {
        cholesterolLDL = $("#65").val($("#67").val() - $("#66").val()).val();
    }
    if ($("#62").val() !== "" && $("#64").val() !== "") {
        totalHDLRatio = $("#68").val($("#62").val() / $("#64").val()).val();
    }
    if (parseFloat(vddlCholesterol) === parseFloat($("#66").val()) && parseFloat(nonHDL) === parseFloat($("#67").val()) && parseFloat(cholesterolLDL) === parseFloat($("#65").val()) && parseFloat(totalHDLRatio) === parseFloat($("#68").val()) && parseFloat($("#68").val()) !== 0) {
        backgroundColourChangeGood($("#62, #63, #64, #65, #66, #67, #68"));
    } else {
        backgroundColourChangeBad($("#62, #63, #64, #65, #66, #67, #68"));
    }
});


//lipid profile - start

//liver profile - start
//           1 Serum Total Proteins (id="69")
//           2 Albumin, Serum (id="70")
//           3 Globulin, Serum (id="71")
//           4 Albumin/Globulin Ratio (id="72")
//           5 Bilirubin, Total, Serum (id="73")
//           6 SGPT (ALT) (id="74")
//           7 Gamma-GT (id="75")
//           8 Alkaline Phosphatase(ALP) (id="76")
//           9 SGOT (AST) (id="77")
// 1 (69) - 2 (70)= 3 (71)
// 2 (70) / 3 (71)= 4 (72)

$("#69, #70, #71, #72, #73, #74, #75, #76, #77").on("keyup", function () {
    let globulin;
    let albuminGlobulinRatio;

    if ($("#69, #70").val() !== "") {
        globulin = $("#71").val($("#69").val() - $("#70").val()).val();
    }
    if ($("#70, #71").val() !== "") {
        albuminGlobulinRatio = $("#72").val($("#70").val() / $("#71").val()).val();
    }

    if (parseFloat($("#71").val()) === parseFloat(globulin) && parseFloat($("#72").val()) === parseFloat(albuminGlobulinRatio)
        && $("#69").val() !== ""
        && $("#70").val() !== ""
        && $("#73").val() !== ""
        && $("#74").val() !== ""
        && $("#75").val() !== ""
        && $("#76").val() !== ""
        && $("#77").val() !== "") {
        backgroundColourChangeGood($("#69, #70, #71, #72, #73, #74, #75, #76, #77"));
    } else {
        backgroundColourChangeBad($("#69, #70, #71, #72, #73, #74, #75, #76, #77"));
    }

});
//liver profile - end