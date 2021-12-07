import requests
base_url = "https://www.epicgames.com"
r = requests.get(base_url + "/store/backend/static/freeGamesPromotions")


def handledata(data):
    for game in data["Catalog"]["searchStore"]["elements"]:
        if game["price"]["totalPrice"]["discount"] != 0:
            print(game["title"])


handledata(r.json()["data"])
