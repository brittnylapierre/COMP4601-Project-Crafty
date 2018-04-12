import sys
import platform
import json
from pymongo import MongoClient
from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from seleniumrequests import Chrome, Safari
import requests
import re

def scrapeDeserres():
	url = "https://www.deserres.ca/en/brands"
	res =  requests.get(url);
	soup = BeautifulSoup(res.text, "lxml")

	raw_links = soup.find_all("a", href=re.compile("brands\/"))

	brand_links = []
	product_links = [] 
	product_map = {}

	for a in raw_links:
		if a['href'] not in brand_links:
			brand_links.append(a['href'])
			res =  requests.get(a['href']+"?limit=all")
			pageSoup = BeautifulSoup(res.text, "lxml")
			product_headers = pageSoup.find_all("h2", {"class": "product-name"})
			for h2 in product_headers:
				raw_product_links = h2.find_all("a")
				for product_link in raw_product_links:
					if product_link['href'] not in product_links:
						product_links.append(product_link['href'])
						if product_link['data-price']:
							product_map[product_link['href']] = {
								'title': product_link.contents[0],
								'description': "",
								'price': product_link['data-price'],
								'url': product_link['href'],
								'brand': product_link['data-brand'],
								'store': "Deserres"
							}
						else:
							print("error")
							print(product_link.contents)
							break
	#print(product_map)
	print("Done Deserres!!")
	#Save our results
	f = open("deserres.json","w+")
	f.write(json.dumps(list(product_map.values())))
	f.close()

#https://wallacks.com/collections - paginated tables
#	https://wallacks.com/collections/adhesives?page=2

#https://www.staples.ca/en/Arts-Crafts/cat_CG2623_2-CA_1_20001
#https://www.staples.ca/en/Office-Supplies/cat_SC5218_2-CA_1_20001

#https://www.currys.com/catalogpc.htm?Category=shop_by_brand - normal crawler

#https://store.abovegroundartsupplies.com/pages/shop_by_category.html
#	https://store.abovegroundartsupplies.com/departments/charcoal-%7CC1.html?top=0

# http://www.jerrysartarama.com/all-products
#	http://www.jerrysartarama.com/all-products?p=2

#http://www.artshack.ca/index.php/art-supplies.html
#	http://www.artshack.ca/index.php/art-supplies.html?p=2