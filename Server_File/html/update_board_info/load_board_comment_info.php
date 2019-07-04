<?php
include "init_board_info.php";

$Post_Writer = $_POST["Post_Writer"];
$Post_Regdate = $_POST["Post_Regdate"];

$sql = "select * from comment_info where Post_Writer like '".$Post_Writer."' and Post_Regdate like '".$Post_Regdate."' ";
$result = mysqli_query($con,$sql);
$response = array();

if(mysqli_num_rows($result)>0)
{
     while($row = mysqli_fetch_row($result))
     {
        $Writer = $row[1];
        $Comment_Text = $row[2];
        $Regdate = $row[3];        
        
        $code = "포스트 댓글 목록 불러오기 성공";
        array_push($response,array("code"=>$code,"Writer"=>$Writer,"Comment_Text"=>$Comment_Text,"Regdate"=>$Regdate));
     }
      echo json_encode($response);
}

mysqli_close($con);
?>
