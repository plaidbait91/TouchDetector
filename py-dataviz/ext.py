import json
import matplotlib.pyplot as plt
import sys
# import datetime

# def getTime(time):
#     hour = datetime.datetime.fromtimestamp(time/1000.0).hour
#     if 5 <= hour and hour < 11:
#         return 0
#     elif 11 <= hour and hour < 16:
#         return 1
#     elif 16 <= hour and hour < 21:
#         return 2
#     else:
#         return 3

def listToXY(series):
    # start = series[0] // 60000
    start = 0
    s_dict = dict()

    for t in series:
        t_val = t // 60000 - start
        s_dict[t_val] = s_dict.get(t_val, 0) + 1


    x = list(s_dict.keys())
    x.sort()
    f = {i: s_dict[i] for i in x}
    y = list(f.values())

    return (x, y)


f = open("data1.json", "r")
data = f.read()

data = json.loads(data)

launches = list(data[sys.argv[1]]["events"][sys.argv[2]]["Launched"].values()) + list(data["153"]["events"]["com,instagram,android"]["Notif clicked"].values())
scrolls = list(data[sys.argv[1]]["events"][sys.argv[2]]["Scrolled"].values())
notifs = list(data[sys.argv[1]]["events"][sys.argv[2]]["Notif received"].values())


# axes = plt.subplot()
# axes.set_xticks([0, 1, 2, 3])
# axes.set_xticklabels(["Morning", "Afternoon", "Evening", "Night"])

x1, y1 = listToXY(notifs)
x2, y2 = listToXY(launches)
print(len(x1), len(x2))
# y1 = [250 for y in y1]
# print(x1)

# plt.bar(x1, y1, color = "red", width=20)
# plt.bar(x2, y2, color = "blue", width=1)
plt.plot(x2, y2, color = "blue", marker = 'o')
plt.plot(x1, y1, color = "red", marker = 'o')
plt.show()




