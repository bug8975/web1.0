// JavaScript Document
$(function(){
	//需求方内容显示  STAR
   $('.menu').hover(function(){
	   $(this).children('.menu-bd').show();   
   },function(){
	   $(this).children('.menu-bd').hide();
   });
   //需求方内容显示  END
    
   //找商品内容显示及选择  SRAR
  $('.seek').hover(function(){
	   $(this).children('.seek_list').show();
   },function(){
	   $(this).children('.seek_list').hide();
   });
   $('.seek .seek_list li').click(function(){
	  $('.seek_value span').html($(this).text());
      $("#type").val($(this).attr("type"));
       $(this).parent('.seek_list').hide();
   });
   //找商品内容显示及选择  END
   //鼠标滑过所出现的div样式
   $('.yMenuListCon .limore:nth-child(2)').addClass("list2");
   $('.yMenuListCon .limore:nth-child(3)').addClass("list3");
   $('.yMenuListCon .limore:nth-child(4)').addClass("list4");
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
			//滚动条滚动事件
	$(window).scroll(function(){
	var top = $(document).scrollTop();
	if(top==0){
		$(".back_box").hide();
		}else{
		$(".back_box").show();	
			}
	});
  //
  $('.info .navleft').hover(function(){
     $('.indexleft').show();
  },function(){
     $('.indexleft').hide();
  });
  
  //店铺页面 nav部分  STAR
  $('.shop-evaluate-det').hover(function(){
	   $(this).children('.top_sear_bom').slideDown();   
  },function(){
	   $(this).children('.top_sear_bom').slideUp();
  });
  //店铺页面 nav部分  END 
  
   //店铺-卖家服务的选择  SRAR
 $('.seller-service').hover(function(){
	   $(this).children('.service_list').show();
  },function(){
	   $(this).children('.service_list').hide();
  });
 // $('.seller-service .service_list li').click(function(){
//	  $('.service_value span').html($(this).text())
//  }); 
  //找商品内容显示及选择  END
  $('.category a').click(function(){
     $(this).addClass('current').siblings().removeClass('current'); 
  });
  
  //店铺客服
  $('.shop-fixed-im').hover(function(){
	   $(this).children('.shop-fixed-im-hover').show();
   },function(){
	   $(this).children('.shop-fixed-im-hover').hide();
   });
   //更多
 
 $('.classb .more-btn').click(function(){
	if($(this).text()=='更多'){
	   $(this).parent('.classb').addClass('classbmore');
	   $(this).text('收起');	
    }else{
	   $(this).parent('.classb').removeClass('classbmore');
	   $(this).text('更多');
	}
})   
   
  
})
