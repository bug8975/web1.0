// JavaScript Document
$(function(){
	//需求方内容显示  STAR
   $('.menu').hover(function(){
	   $(this).children('.menu-bd').slideDown();   
   },function(){
	   $(this).children('.menu-bd').slideUp();
   });
   //需求方内容显示  END
    
   //找商品内容显示及选择  SRAR
  $('.seek').hover(function(){
	   $(this).children('.seek_list').show();
   },function(){
	   $(this).children('.seek_list').hide();
   });
   $('.seek .seek_list li').click(function(){
	  $('.seek_value span').html($(this).text())
   }); 
   //找商品内容显示及选择  END
   
   //全部分类显示  STAR
   $(".indexleftList li").hover(function(){
			$(".yMenuListCon").fadeIn();
			var index=$(this).index(".indexleftList li");
			if (!($(this).hasClass("menulihover"))) {
				$($(".yBannerList")[index]).css("display","block").siblings().css("display","none");
				$($(".yBannerList")[index]).removeClass("ybannerExposure");
				setTimeout(function(){
				$($(".yBannerList")[index]).addClass("ybannerExposure");
				},60)
			}else{	
			}
			$(this).addClass("menulihover").siblings().removeClass("menulihover");
			$($(".limore")[index]).fadeIn().siblings().fadeOut();
		},function(){
			
		})
		$(".indexleft").mouseleave(function(){
			$(".yMenuListCon").fadeOut();
			$(".limore").fadeOut();
			$(".indexleftList li").removeClass("menulihover");

  });
  //全部分类显示  END  
})
