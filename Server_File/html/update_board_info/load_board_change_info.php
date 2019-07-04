<?php
include "init_board_info.php";

$Writer = $_POST["Writer"];
$Regdate = $_POST["Regdate"];

$sql = "select * from post_info where Writer like '".$Writer."' and Regdate like '".$Regdate."' ";
$result = mysqli_query($con,$sql);
$response = array();

if(mysqli_num_rows($result)>0)
{ //어차피 수정을 위해서 1개의 포스트만 불러올꺼라 while 해서 불러올필요는 없지만, 귀찮으니 이렇게해보자.
     while($row = mysqli_fetch_row($result))
     {
        $Writer = $row[1];
        $Post_Image_Url = $row[2];
        $Post_Text = $row[3];
        $Like_Num = $row[4];
        $Comment_Num = $row[5];
        $Regdate = $row[6];

        $code = "포스트 1개 불러오기 성공";
        array_push($response,array("code"=>$code,"Writer"=>$Writer,"Post_Image_Url"=>$Post_Image_Url,
                "Post_Text"=>$Post_Text,"Like_Num"=>$Like_Num,"Comment_Num"=>$Comment_Num,"Regdate"=>$Regdate));
     }
      echo json_encode($response);
}


mysqli_close($con);
?>
