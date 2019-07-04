<?php
include "init_board_info.php";

$Writer = $_POST["Writer"];
$Regdate = $_POST["Regdate"];
$Comment_Text = $_POST["Comment_Text"];

$response = array();

         $sql = "update comment_info set
         Comment_Text =  '".$Comment_Text."'
         where Writer like '".$Writer."' and Regdate like '".$Regdate."' ";

        $result = mysqli_query($con,$sql);

        $code = "post comment successfully updated!";
        $message = "포스트 댓글이 성공적으로 수정되었습니다.";
        array_push($response, array("code"=>$code,"message"=>$message));
        echo json_encode($response);

mysqli_close($con);

?>
