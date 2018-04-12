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

url = "https://www.deserres.ca/en/brands"
res =  requests.get(url);
soup = BeautifulSoup(res.text, "lxml")
#wraps = soup.find_all("div", {"class": "content-wrap"})
#links = []
#if wraps and len(wraps) > 0: 
#	wrap = wraps[0]
raw_links = soup.find_all("a", href=re.compile("brands"))

links = []
for a in raw_links:
	if a['href'] not in links:
		links.append(a['href'])
print(links)
