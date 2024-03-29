package com.example.demo.controllers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entities.Auction;
import com.example.demo.entities.BiddingTransaction;
import com.example.demo.entities.Category;
import com.example.demo.entities.Product;
import com.example.demo.entities.ProductReg;
import com.example.demo.entities.ProductWithBid;
import com.example.demo.entities.Question;
import com.example.demo.entities.User;
import com.example.demo.entities.UserReg;
import com.example.demo.entities.UserType;
import com.example.demo.services.BiddingService;
import com.example.demo.services.CategoryService;
import com.example.demo.services.ProductService;
import com.example.demo.services.UserService;

@CrossOrigin(origins = {"http://localhost:3000" ,"https://main.d2lgoomnh1lohm.amplifyapp.com"})
@RestController
public class ProductController {
	
	@Autowired
	ProductService pserv;
	
	@Autowired
	CategoryService cserv;
	
	@Autowired
	UserService userv;
	
	@Autowired
	BiddingService bserv;
	
	@PostMapping("/regproduct")
	public Product regSeller(@RequestBody ProductReg product)
	{
		Category category_id= cserv.getById(Integer.parseInt(product.getCategory_id()));
		System.out.println(category_id.getCategory_id() + " " + category_id.getCategory_name() + " " + product.getSeller_id());
		User seller_id=userv.getById(Integer.parseInt(product.getSeller_id()));

		Product p=new Product(product.getProduct_name(),category_id,product.getDescription(),Float.parseFloat(product.getBase_price()),seller_id,product.getStatus());
		

		return pserv.saveProduct(p);
	}
	
	@PostMapping("/uploadproductimage1/{P_Id}")
	public boolean upload1(@PathVariable("P_Id") int P_Id,@RequestBody MultipartFile file)
	{
		boolean flag = true;
		try {
			flag = pserv.upload1(P_Id,file.getBytes());
			
			System.out.println(file.getBytes());
		}
		catch(Exception e)
		{
			flag=false;
		}
		return flag;
	}
	
	@PostMapping("/uploadproductimage2/{P_Id}")
	public boolean upload2(@PathVariable("P_Id") int P_Id,@RequestBody MultipartFile file)
	{
		boolean flag = true;
		try {
			flag = pserv.upload2(P_Id,file.getBytes());
		}
		catch(Exception e)
		{
			flag=false;
		}
		return flag;
	}

	
	@PostMapping("/uploadproductimage3/{P_Id}")
	public boolean upload3(@PathVariable("P_Id") int P_Id,@RequestBody MultipartFile file)
	{
		boolean flag = true;
		try {
			flag = pserv.upload3(P_Id,file.getBytes());
		}
		catch(Exception e)
		{
			flag=false;
		}
		return flag;
	}
	
	@GetMapping("/pendingproductsforapproval")
	public List<Product> pendingProducts()
	{
		return pserv.pendingProducts();
	}
	
	@PutMapping("/approveproduct/{P_Id}")
	public int approveProduct(@PathVariable("P_Id") int P_Id)
	{
		return pserv.approveProduct(P_Id);
	}
	
	@PutMapping("/denyproduct/{P_Id}")
	public int denyProduct(@PathVariable("P_Id") int P_Id)
	{
		return pserv.denyProduct(P_Id);
	}
	
	@GetMapping("/approvedproducts/{seller_id}")
	public List<Product> approvedProducts(@PathVariable("seller_id") int seller_id)
	{
		//User seller_id=userv.getById(s_id);
		return pserv.approvedProducts(seller_id);
	}

	@PostMapping("/startauction")
	public int startAuction(@RequestBody Auction auction)
	{
		System.out.println(auction.getP_Id() + " " + auction.getStart_date() + " " + auction.getEnd_date());
		Date start_date = Date.valueOf(auction.getStart_date());
		Date end_date = Date.valueOf(auction.getEnd_date());
		int P_Id = auction.getP_Id();
		
		
		return pserv.startAuction(start_date,end_date,P_Id);
	}
	
	@GetMapping("/products")
	public List<ProductWithBid> products()
	{
		List<Product> products=pserv.current_date_products();
		List<ProductWithBid> productsWithBids = new ArrayList<>();
		for(Product p:products)
		{
			System.out.println(p.getP_Id());
			BiddingTransaction bt= bserv.findMaxBid(p.getP_Id());
			//System.out.println(bt.getBid_price());
			productsWithBids.add(new ProductWithBid(p,bt));
			
		}
		
		return productsWithBids;
	}
	
	@GetMapping("/products/filter/{category_id}")
	public List<ProductWithBid> category_wise_products(@PathVariable("category_id") int category_id)
	{   
		List<Product> products;
		if(category_id != 0)
		{		 products=pserv.category_wise_products(category_id);
        }
		
		else
		{
			products=pserv.current_date_products();
		}
		List<ProductWithBid> productsWithBids = new ArrayList<>();
		for(Product p:products)
		{
			System.out.println(p.getP_Id());
			BiddingTransaction bt= bserv.findMaxBid(p.getP_Id());
			//System.out.println(bt.getBid_price());
			productsWithBids.add(new ProductWithBid(p,bt));
			
		}
		
		return productsWithBids;
	}
	
	@GetMapping("/products/search/{search_string}")
	public List<ProductWithBid> product_search(@PathVariable("search_string") String search_string)
	{
		System.out.println("in search");
		List<Product> products=pserv.search_products(search_string);
		List<ProductWithBid> productsWithBids = new ArrayList<>();
		for(Product p:products)
		{
			System.out.println(p.getP_Id());
			BiddingTransaction bt= bserv.findMaxBid(p.getP_Id());
			//System.out.println(bt.getBid_price());
			productsWithBids.add(new ProductWithBid(p,bt));
			
		}
		
		return productsWithBids;
	}
	
	
	
	
	
	@GetMapping("/ongoingauctionforseller/{seller_id}")
	public List<ProductWithBid> ongoingAuctionForSeller(@PathVariable("seller_id") int seller_id)
	{
		List<Product> products=pserv.ongoingAuctionForSellers(seller_id);
		List<ProductWithBid> productsWithBids = new ArrayList<>();
		for(Product p:products)
		{
			System.out.println(p.getP_Id());
			BiddingTransaction bt= bserv.findMaxBid(p.getP_Id());
			//System.out.println(bt.getBid_price());
			productsWithBids.add(new ProductWithBid(p,bt));
			
		}
		
		return productsWithBids;
	}
	
}
