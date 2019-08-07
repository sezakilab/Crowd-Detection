import requests
import json
import pandas as pd
import time
import datetime
import threading
import sys
import random

def current_datetime():
    now = datetime.datetime.now()
    return now.strftime("%Y/%m/%d %H:%M:%S")

class VirtualDevice(threading.Thread):
    def __init__(self, uid=0, interval=1, url='http://127.0.0.1:5000/post_data', live=False):
        super(VirtualDevice, self).__init__()
        self.stop_event = threading.Event()
        self.setDaemon(True)

        self.uid   = uid
        self.url   = url
        self.live  = live
        self.users = ["Ishida", "Song", "Sun", "Tian", "Zhang", "testname"]
        self.user  = self.users[self.uid%len(self.users)]
        self.interval = interval
        
    def stop(self):
        self.stop_event.set()

    def run(self):
        df_bt_device   = pd.read_csv('db/bluetooth_device.csv',index_col=0)
        df_scanner_loc = pd.read_csv('db/bluetooth.csv',index_col=0)

        headers = {"Content-Type" : "application/json"}
        scanner_locs = df_scanner_loc[df_scanner_loc['username'].isin([self.user])]
        bt_devices   = df_bt_device[df_bt_device['username'].isin([self.user])]

        while not self.stop_event.is_set():
            print("[Info] Start a new loop")
            for index, row in scanner_locs.iterrows():
                devices = []
                discovered_bt_devices = bt_devices[bt_devices.user_to_device_id == index]
                
                extra_rssi = 0
                extra_lat  = 0
                extra_long = 0
                if self.uid > len(self.users):
                    extra_rssi = random.uniform(-5,5.0)
                    extra_lat  = random.uniform(-0.0001,0.0001)
                    extra_long = random.uniform(-0.0001,0.0001)
                
                for i, d in discovered_bt_devices.iterrows():
                    date = current_datetime()
                    if self.live == False:
                        date = d.date
                    devices.append({'address':d.mac_address,
                                    'rssi'   :d.rssi + extra_rssi,
                                    'date'   :date,
                                    'lat'    :d.lat  + extra_lat,
                                    'long'   :d.long + extra_long})
                date = current_datetime()
                if self.live == False:
                    date = row.date
                obj = {'date'    :date,
                       'username':row.username,
                       'lat'     :row.lat  + extra_lat,
                       'long'    :row.long + extra_long,
                       'device'  :devices}
                json_data = json.dumps(obj).encode("utf-8")
                response = requests.post(self.url, data=json_data, headers=headers)
                time.sleep(self.interval)
                print("upload VD-"+str(self.uid), date, response.status_code)

if __name__ == '__main__':
    args = sys.argv

    vs_number     = 6
    interval_sec  = 1
    live = False
    url  = 'http://127.0.0.1:5000/post_data';
    
    for i,v in enumerate(args):
        if v == "-n" or v == '--number':
            vs_number = int(args[i+1])
        elif v == "-i" or v == '--interval':
            interval_sec = float(args[i+1])
        elif v == "-l" or v == '--live':
            live = True
        elif v == "-u" or v == "--url":
            url = str(args[i+1])
         
    print("--------")
    print("[Config]")
    print("Number of Virtual Scanner: ", vs_number)
    print("Scan Interval : ", interval_sec)
    print("Live Mode     : ", live)
    print("URL           : ", url)
    print("--------")
    
    vds = []
    for user_id in range(0,vs_number):
        vd = VirtualDevice(uid=user_id, interval=interval_sec, live=live, url=url)
        vd.start()
        print("start: VD-"+str(vd.uid))
        vds.append(vd)
        time.sleep(0.1)

    print("[Info] To stop virtual scanners, please push an enter button once after typing `q` or `exit.`")
    while True:
        keyboard_input = input()
        if keyboard_input=="exit" or keyboard_input=='q':
            for vd in vds:
                vd.stop()
                print("stop: VD-"+str(vd.uid))
            break
