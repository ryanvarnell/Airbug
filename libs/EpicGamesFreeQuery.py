import requests
base_url = "https://www.epicgames.com"
r = requests.get(base_url + "/store/backend/static/freeGamesPromotions")


def handledata(data):
    for game in data["Catalog"]["searchStore"]["elements"]:
        if game["price"]["totalPrice"]["originalPrice"] != 0:
            if game["price"]["totalPrice"]["originalPrice"] == game["price"]["totalPrice"]["discount"]:
                print(game["title"])


handledata(r.json()["data"])
