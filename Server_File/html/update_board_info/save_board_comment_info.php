<?php
include "init_board_info.php";

$Writer = $_POST["Writer"];
$Comment_Text = $_POST["Comment_Text"];
$Regdate = $_POST["Regdate"];
$Post_Writer = $_POST["Post_Writer"];
$Post_Regdate = $_POST["Post_Regdate"];

$response = array();

        $sql = "insert into comment_info(Writer,Comment_Text,Regdate,Post_Writer,Post_Regdate) values('".$Writer."','".$Comment_Text."','".$Regdate."','".$Post_Writer."','".$Post_Regdate."');";
        $result = mysqli_query($con,$sql);

        $code = "successfully comment saved!";
        $message = "댓글이 성공적으로 등록되었습니다.)";
        array_push($response, array("code"=>$code,"message"=>$message));
        echo json_encode($response);

mysqli_close($con);

?>
