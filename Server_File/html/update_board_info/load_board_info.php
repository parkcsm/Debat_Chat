<?php
include "init_board_info.php";

$sql = "select * from post_info";
$result = mysqli_query($con,$sql);
$response = array();

if(mysqli_num_rows($result)>0)
{
     while($row = mysqli_fetch_row($result))
     {
        $Writer = $row[1];
        $Post_Image_Url = $row[2];
        $Post_Text = $row[3];
        $Like_Num = $row[4];
        $Comment_Num = $row[5];
        $Regdate = $row[6];

        $code = "포스트목록 불러오기 성공";
        array_push($response,array("code"=>$code,"Writer"=>$Writer,"Post_Image_Url"=>$Post_Image_Url,
        	"Post_Text"=>$Post_Text,"Like_Num"=>$Like_Num,"Comment_Num"=>$Comment_Num,"Regdate"=>$Regdate));
     }
      echo json_encode($response);
}


mysqli_close($con);
?>
