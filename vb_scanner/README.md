# Virtual Bluetooth Scanner
This is a Python script for uploading Bluetooth device information, which is collected around Shimokitazawa, to a [data collection server](https://github.com/sezakilab/Crowd-Detection).

## Requirements
* Python 3
    * pandas
    
## Examples

### Start
Generate six virual-scanners and upload data to URL by every 1 second.
```
python main.py
```

Generate six virtual-scanners and upload data to the default URL by every 1 second using current timestamp instead of the timestamp which is used in the dataset.
```
python main.py -l
```

Generate ten virtual-scanners and upload data to "http://127.0.0.1:5000/post_data" by every 5 seconds using current timestamp instead of the dataset's timestamp.
```
python main.py -n 10 -i 5 -l -u http://127.0.0.1:5000/post_data
```

### Stop
To stop virtual-scanners, please push an enter button once after typing `q` or `exit.`

## Options
|Option(Full)|Option(Short)|Default|Description|
|:---|:---|:---|:---|
|--interval|-i|1.0|An interval of date upload|
|--number|-n|6|A number of virtual scanners|
|--live|-l|(none)|A flag for using the current timestamp instead of the timestamp of the dataset|
|--url|-u|http://127.0.0.1:5000/post_data |A URL of a data collection server|
