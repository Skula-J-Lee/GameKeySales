<?php
	session_start();
	$_SESSION['name'];
	$_SESSION['email'];
	$_SESSION['money'] = number_format("999999999");
	
    //오류발생 시 알려주는 함수, 사용할 경우 ini_set '1' 하고 주석 삭제할 것
    //error_reporting(E_ALL);
    ini_set('display_errors',0);
	
	include('dbcon.php');
	
	$receivemoney = $con->prepare('select money from banktable');
	$receivemoney->execute();
	$moneybank = $receivemoney->fetchAll(PDO::FETCH_NUM);
	
	if ($receivemoney->rowCount() == 0) {
		$_SESSION['money'] = number_format("999999999");
	} else {
		$_SESSION['money'] = number_format($moneybank[0][0]);
	}
	
	$subTitleMSG = "아래 입력 후 구매신청 하기를 선택하세요.<br>24시간 계좌이체 즉시 발송됩니다.";
	
	$firstContentMSG = "<input type=\"text\" name=\"umame\" class=\"name_input\" placeholder=\"이름\" required><br>";
	$secondContentMSG = "<input type=\"text\" name=\"uemail\" class=\"mail_input\" placeholder=\"이메일\" required>";
	
	$firstInputMSG = "<input class=\"first_input\" type=\"submit\" name=\"submitbuy\" value=\"구매신청 하기\">";
	$secondInputMSG = "<input class=\"second_input\" type=\"submit\" name=\"submitcheck\" value=\"구매내역 확인\">";

	
	$cssMSG = "<link rel=\"stylesheet\" type=\"text/css\" href=\"css/main.css\">";
	$mobileinfo = "<div class=\"mobileinfo\"><p>Escape from Tarkov<span><br>스탠다드 (Global Key) <b>[\\{$_SESSION['money']}]</b></span></p></div>";
	
	// 전송 누르면
	if( ($_SERVER['REQUEST_METHOD'] == 'POST'))
	{
		if (isset($_POST['submitbuy']) || isset($_POST['submitcheck']))
		{
			$name = $_POST['umame'];
			$email = $_POST['uemail'];
			
			$_SESSION['name'] = $name;
			$_SESSION['email'] = $email;
			
			// 이메일 체크
			$checkMail = filter_Var($email, FILTER_VALIDATE_EMAIL);
			
			if ($checkMail == true)
			{
				// 이메일 형식이 맞을 경우
				// CSS 수정 (왼쪽 글부분 없애기)
				$cssMSG = "<link rel=\"stylesheet\" type=\"text/css\" href=\"css/request.css\">";
				
				if(isset($_POST['submitbuy']))
				{
					// 구매신청 하기
					$subTitleMSG = "입금자명과 기입한 성명이 동일 해야합니다.<br>아래 정보가 맞습니까?";
					
					$firstContentMSG = "이&nbsp;&nbsp;&nbsp;름 : ".$name;
					$secondContentMSG = "이메일 : ".$email;

					$firstInputMSG = "<input type=\"submit\" class=\"first_input\" name=\"submitpop\" value=\"네, 맞아요\">";
					$secondInputMSG = "<input type=\"button\" class=\"second_input\" value=\"아니에요\" onclick=\"location.href='./'\">";
				}
				if(isset($_POST['submitcheck']))
				{
					//  구매내역 확인
					$cssMSG = "<link rel=\"stylesheet\" type=\"text/css\" href=\"css/check.css\">";
					$scriptMSG = "<script type=\"text/javascript\" src=\"js/check.js\"></script>";
					
					$subTitleMSG = "아래 최근 신청하신 목록입니다.";
					
					// DB 연동 부분
					$stmt = $con->prepare('SELECT regdate, process FROM usertable WHERE name=:name AND email=:email');
					$stmt->bindParam(':name', $name);
					$stmt->bindParam(':email', $email);
					$stmt->execute();

					$infoROW = $stmt->rowCount();
					
					if($infoROW > 0) {
					    $infolist = $stmt->fetchAll(PDO::FETCH_NUM);
					    $dateMSG = "신청날짜 : ";
					    $depMSG = "입금현황 : ";
					    $proMSG = "처리현황 : ";
					    $enterMSG = "<br>";
						$contentdata = "";
						
					    $titlepop = "\"<span>{$name}</span>\" 님이<br>신청하신 목록 입니다.";

						// 데이터 가져오는 반복문
					    for ($i=$infoROW-1; $i>=0; $i--){
							$contentdata = $contentdata.$dateMSG.$infolist[$i][0].$enterMSG;
							
							$depstate = substr($infolist[$i][1], 0, 1);
							$prostate = substr($infolist[$i][1], 1, 2);
							
							if ($depstate == "Y" || $depstate == "O") {
								$depstateMSG = "입금 완료";
							} else if ($depstate == "L") {
								$depstateMSG = "금액 부족";
							} else {
								$depstateMSG = "확인 중";
							}
							$contentdata = $contentdata.$depMSG.$depstateMSG.$enterMSG;
							
							if ($prostate == "Y") {
								$prostateMSG = "처리 완료";
							} else {
								$prostateMSG = "확인 중";
							}
							
							if ($i == 0) {
								$enterMSG = "";
							}
							
							if ($i == $infoROW - 1) {
								$firstContentMSG = $dateMSG.$infolist[$i][0];
								$secondContentMSG = $depMSG.$depstateMSG;
								$thirdContentMSG = "<dd>".$proMSG.$prostateMSG."</dd>";
								$firstInputMSG = "<input type=\"button\" class=\"first_input\" id=\"pop\" value=\"목록 더 보기\">";
								$secondInputMSG = "<input type=\"button\" class=\"second_input\" value=\"돌아가기\" onclick=\"location.href='./'\">";
							}
							$contentdata = $contentdata.$proMSG.$prostateMSG.$enterMSG.$enterMSG;
					    }
						$contentpop = $contentdata;
						$endpop = "<div class=\"moreButtom\">닫&emsp;기</div>";
					}
					else
					{
						$MSG = "작성한 정보의 리스트가 없습니다";
				    }
				}
			}
			else
			{
				// 이메일 형식이 아닐 경우
				$errMSG = "이메일 주소가 옳지 않습니다.";
			}
		}
		if (isset($_POST['submitpop']))
		{
			$name = $_SESSION['name'];
			$email = $_SESSION['email'];

			$cssMSG = "<link rel=\"stylesheet\" type=\"text/css\" href=\"css/request.css\">";
		    $scriptMSG = "<script type=\"text/javascript\" src=\"js/acount.js\"></script>";
			
			// 구매신청 하기
			$subTitleMSG = "입금자명과 기입한 성명이 동일 해야합니다.<br>아래 정보가 맞습니까?";
					
			$firstContentMSG = "이&nbsp;&nbsp;&nbsp;름 : ".$name;
			$secondContentMSG = "이메일 : ".$email;

			$firstInputMSG = "<input type=\"submit\" class=\"first_input\" name=\"submitpop\" value=\"네, 맞아요\">";
			$secondInputMSG = "<input type=\"button\" class=\"second_input\" value=\"아니에요\" onclick=\"location.href='./'\">";

			$receivebank = $con->prepare('select * from banktable');
			$receivebank->execute();
			$bank = $receivebank->fetchAll(PDO::FETCH_NUM);
			
			//게임키 확인
			$gamekeychk = $con->prepare('select id, gamekey from gamekeytable WHERE used=\'N\'');
			$gamekeychk->execute();
					
			if ($receivebank->rowCount() == 0) {
				$bankname = "";
				$depname = "";
				$account = "";
				$money = "";
				
			} else {
				$bankname = $bank[0][1];
				$depname = $bank[0][2];
				$account = $bank[0][3];
				$money = number_format($bank[0][4]);
			}
					
			try {
				$insertuser = $con->prepare('INSERT INTO usertable(name, email) VALUES(:name, :email)');
				$insertuser->bindParam(':name', $name);
				$insertuser->bindParam(':email', $email);

				if($insertuser->execute()) {
					$titlepop = "\"<span>{$name}</span>\" 님,<br>신청이 완료 되었습니다.";
					$contentpop = "<p>&ensp;입금 안내</p><span>&emsp;입금은행</span> : {$bankname}<br><span>&emsp;계좌번호</span> : {$account}<br><span>&emsp;예금주명</span> : {$depname}<br><span>&emsp;입금금액</span> : {$money}";
						
					if ($gamekeychk->rowCount() == 0) {
						$contentpop = $contentpop."<div style=\"margin-top:20px; font-family:nanumB; line-height:20px; color:#e5a200;\">현재 공급해드릴 키가 남아있지 않습니다.<br>계좌이체시 관리자가 확인 후 키가 지급됩니다.</div>";
					} else {
						$contentpop = $contentpop."<div>입금을 완료하시면 이메일로 코드가 발송됩니다.</div>";
					}
						
					$endpop = "<div class=\"moreButtom\" onclick=\"location.href='./'\">홈 으 로</div>";
				} else {
					$errMSG = "에러가 발생하였습니다.";
				}
			} catch (PDOException $e) {
				$errMSG = "에러가 발생하였습니다.";
			}
		}
	}
?>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<meta name="naver-site-verification" content="12e822cb889144bac04af62d69279dcb78abeac4" />
    <title>Keypara: 타르코프 - 24시간 자동판매</title>
	<meta name="description" content="타르코프 스탠다드 글로벌 키를 24시간 계좌이체 시 즉시 발송합니다.">
    <link rel="stylesheet" href="css/index.css">
    <link rel="stylesheet" href="css/reset.css">
	<?php
		if (isset($cssMSG)) echo $cssMSG;
    ?>
	<script type="text/javascript" src="js/jquery-3.4.1.min.js"></script>
	<script type="text/javascript" src="js/jquery.nicescroll.js"></script>
	<?php
		if (isset($scriptMSG)) echo $scriptMSG;
	?>
    <link rel="icon" href="images/fv.ico" type="image/png">
</head>
  <?php
    if (isset($errMSG)) echo "<script type=\"text/javascript\">alert(\"$errMSG\"); location.href='./'; </script>";
	if (isset($MSG)) echo "<script type=\"text/javascript\">alert(\"$MSG\"); location.href='./'; </script>";
  ?>
<body>
    <div id="wrap">
		<div id="inner_wrap">
			<div id="main_left">
				<h3>Escape from Tarkov<span> : 스탠다드 (Global Key)</span></h3>
				<h4><?php echo "\\{$_SESSION['money']}"; ?></h4>
				<h1>"스토리 중심의 <span>대규모 멀티 플레이</span> 온라인 1인칭 <span>액션 FPS 게임</span>"</h1>
				<h2>러시아와 유럽연합의 합작으로 만들어낸 'Norvinsk'의 외곽 도시 'Tarkov'에서 정치 분쟁이 일어난다.<br>
				타르코프는 BEAR와 USEC, 두 용병단 사이의 전쟁터로 변질되고 살아남은 용병들은 사령부마저 와해된 채 봉쇄된 
				타르코프에 갇히게 된다. 타르코프 안에서는 USEC과 BEAR간의 싸움 뿐만이 아니라 수많은 갱단들의 세력 유지를 
				위한 영토 분쟁 또한 거세지고 있는 상황이며, 선택에 따라 <span>USEC, 또는 BEAR 소속의 플레이어는 완전히 몰락하여 
				무법지대가 된 타르코프에서 탈출하기 위해 의뢰를 수행</span>하여야 한다.</h2> 
			</div>
			<div id="main_right">
				<dl class="right_top">
					<dt><img src="images/logo.png" width="150" height="150" oncontextmenu="return false" ondragstart="return false"></dt>
					<dd>타르코프</dd>
					<dd><h1><?php echo $subTitleMSG;?></h1></dd>
				</dl>
				<form method="POST" class="form_div" action="<?php $_PHP_SELF ?>" >
					<dl class="form_input">
						<dt><?php echo $firstContentMSG;?></dt>
						<dd><?php echo $secondContentMSG;?></dd>
						<?php if (isset($thirdContentMSG)) echo $thirdContentMSG;?>
					</dl>
					<dl class="right_bottom">
						<dt><?php echo $firstInputMSG;?></dt>
						<dd><?php echo $secondInputMSG;?></dd>
					</dl>
				</form>
			</div>
        </div>
			<div class="morepop">
				<div class="moretitle">
					<?php if (isset($titlepop)) echo $titlepop;?>
				</div>
				<div class="morecontent">
					<?php if (isset($contentpop)) echo $contentpop;?>
				</div>
				<?php if (isset($endpop)) echo $endpop;?>
			</div>
			<?php if (isset($mobileinfo)) echo $mobileinfo;?>
			<dl class="kakao" onclick="window.open('https://open.kakao.com/o/sHhPaKdc')">
				<dt></dt>
				<dd>상담문의</dd>
			</dl>
        <div class="discord" >
            <div onclick="window.open('https://discord.gg/F9CA8CX')")></div>
        </div>
    </div>
</body>
</html>