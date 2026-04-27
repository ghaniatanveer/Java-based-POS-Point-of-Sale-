# Departmental Store Dataset (Open Source Demo)

This dataset is auto-seeded in the database when the app starts (if product barcodes do not already exist).

## Currency Note
- All prices are in **PKR (Pakistani Rupees)**.

## How Product Lookup Works
- The POS search uses **product name** and **barcode/product code**.
- Enter the code in the sale/product search box (for example: `DS1001`).
- Matching product will be shown and can be added to cart.

## Product Code Catalog (Expanded - PKR)

| Code | Product Name | Category | Unit Price (PKR) |
|---|---|---|---:|
| DS1001 | Cola 500ml | Beverages | 120 |
| DS1002 | Orange Juice 1L | Beverages | 340 |
| DS1003 | Mineral Water 1.5L | Beverages | 110 |
| DS1004 | Energy Drink 250ml | Beverages | 280 |
| DS1005 | Green Tea Box | Beverages | 560 |
| DS1006 | Mango Drink 1L | Beverages | 260 |
| DS1007 | Lemon Soda 500ml | Beverages | 140 |
| DS1008 | Rooh Afza 800ml | Beverages | 490 |
| DS2001 | Potato Chips 80g | Snacks | 120 |
| DS2002 | Salted Peanuts 150g | Snacks | 210 |
| DS2003 | Chocolate Cookies 200g | Snacks | 240 |
| DS2004 | Nachos 150g | Snacks | 180 |
| DS2005 | Popcorn Butter 90g | Snacks | 170 |
| DS2006 | Crackers 120g | Snacks | 95 |
| DS2007 | Chocolate Wafer 6 Pack | Snacks | 150 |
| DS2008 | Dry Fruit Mix 200g | Snacks | 680 |
| DS3001 | Full Cream Milk 1L | Dairy | 290 |
| DS3002 | Yogurt Plain 500g | Dairy | 260 |
| DS3003 | Cheddar Cheese 200g | Dairy | 780 |
| DS3004 | Butter 200g | Dairy | 620 |
| DS3005 | Eggs 12 Pack | Dairy | 420 |
| DS3006 | Cream 200ml | Dairy | 190 |
| DS3007 | Lassi 1L | Dairy | 240 |
| DS3008 | Mozzarella Cheese 200g | Dairy | 760 |
| DS4001 | White Bread Loaf | Bakery | 140 |
| DS4002 | Brown Bread Loaf | Bakery | 170 |
| DS4003 | Croissant 4 Pack | Bakery | 420 |
| DS4004 | Muffin Chocolate 2 Pack | Bakery | 260 |
| DS4005 | Burger Buns 6 Pack | Bakery | 210 |
| DS4006 | Cake Rusk 500g | Bakery | 380 |
| DS4007 | Tea Cake Small | Bakery | 480 |
| DS4008 | Donut Glazed | Bakery | 120 |
| DS5001 | Dishwash Liquid 500ml | Household | 350 |
| DS5002 | Laundry Powder 1kg | Household | 760 |
| DS5003 | Floor Cleaner 1L | Household | 620 |
| DS5004 | Tissue Box | Household | 170 |
| DS5005 | Aluminum Foil Roll | Household | 320 |
| DS5006 | Toilet Cleaner 500ml | Household | 390 |
| DS5007 | Garbage Bags Large 30pc | Household | 280 |
| DS5008 | Glass Cleaner 500ml | Household | 340 |
| DS6001 | Shampoo 400ml | Personal Care | 890 |
| DS6002 | Soap Bar 125g | Personal Care | 120 |
| DS6003 | Toothpaste 120g | Personal Care | 260 |
| DS6004 | Toothbrush Medium | Personal Care | 140 |
| DS6005 | Hand Wash 250ml | Personal Care | 290 |
| DS6006 | Hair Oil 200ml | Personal Care | 370 |
| DS6007 | Face Wash 100ml | Personal Care | 520 |
| DS6008 | Shaving Foam 200ml | Personal Care | 540 |
| DS7001 | Basmati Rice 5kg | Staples | 3200 |
| DS7002 | Wheat Flour 5kg | Staples | 1450 |
| DS7003 | Cooking Oil 1L | Staples | 680 |
| DS7004 | Sugar 1kg | Staples | 180 |
| DS7005 | Salt Iodized 800g | Staples | 95 |
| DS7006 | Daal Chana 1kg | Staples | 420 |
| DS7007 | Daal Masoor 1kg | Staples | 390 |
| DS7008 | Tea Premium 475g | Staples | 1190 |
| DS8001 | Chicken Nuggets 750g | Frozen | 980 |
| DS8002 | Paratha Family Pack | Frozen | 560 |
| DS8003 | French Fries 1kg | Frozen | 740 |
| DS8004 | Frozen Peas 500g | Frozen | 320 |
| DS8005 | Ice Cream Vanilla 1L | Frozen | 690 |
| DS8006 | Frozen Samosa 20pc | Frozen | 610 |
| DS9001 | Baked Beans 400g | Canned | 380 |
| DS9002 | Tuna Can 170g | Canned | 520 |
| DS9003 | Tomato Paste 400g | Canned | 260 |
| DS9004 | Sweet Corn 400g | Canned | 290 |
| DS9005 | Mushroom Can 400g | Canned | 420 |
| DS9006 | Mixed Fruit Can 825g | Canned | 650 |
| DSA001 | Baby Diapers Small 34 | Baby Care | 1650 |
| DSA002 | Baby Diapers Medium 30 | Baby Care | 1720 |
| DSA003 | Baby Wipes 72 Sheets | Baby Care | 390 |
| DSA004 | Baby Lotion 200ml | Baby Care | 540 |
| DSA005 | Baby Shampoo 200ml | Baby Care | 520 |
| DSA006 | Baby Powder 200g | Baby Care | 360 |
| DSB001 | Notebook A4 100 Pages | Stationery | 180 |
| DSB002 | Ball Pen Blue 10 Pack | Stationery | 220 |
| DSB003 | Permanent Marker | Stationery | 95 |
| DSB004 | Stapler Medium | Stationery | 260 |
| DSB005 | Glue Stick 35g | Stationery | 110 |
| DSB006 | Printer Paper A4 500 Sheets | Stationery | 1450 |

## Important Notes
- If you already have old database data, delete `database/posdb.mv.db` once and restart app to load fresh demo dataset.
- Existing products with same barcode are safely preserved because seed uses barcode keys.
