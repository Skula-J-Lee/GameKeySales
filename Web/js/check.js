$(function () {
	$(".morecontent").niceScroll({cursorcolor:"#999999"});
});


$(document) .ready(function(){
	$("#pop") .click(function(){
		$(".morepop") .stop().animate({marginBottom:-250});
		$(".morepop") .show();
	});
	
	$(".moreButtom") .click(function(){
		$(".morepop") .stop().animate({marginBottom:-2000});
	});
});
