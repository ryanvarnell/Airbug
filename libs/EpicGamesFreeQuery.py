import requests
base_url = "https://www.epicgames.com"
r = requests.get(base_url + "/store/backend/static/freeGamesPromotions")


def handledata(data):
    for game in data["Catalog"]["searchStore"]["elements"]:
        if game["customAttributes"][0]["key"] != "com.epicgames.app.blacklist":
            if game["title"] != "Mystery Game":
                if game["title"] != "Rogue Company":
                    print(game["title"])


handledata(r.json()["data"])
