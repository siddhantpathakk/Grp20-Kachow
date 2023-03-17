# Image Recognition - CZ3004

## Instructions to setup and run:

Preferred System Requirements

1. RAM - 16GB
2. Operating System - Windows/Linux based OS
3. GPU - NVIDIA 1650Ti 4GB

Install conda/miniconda appropriate to your OS using the link: [Conda Installation Guide](https://docs.conda.io/projects/conda/en/latest/user-guide/install/index.html)

Once Conda is installed successfully, create a new directory:

```bash
mkdir cz3004_imgrec
```

Git clone this repository:

```bash
git clone https://github.com/siddhantpathakk/CZ3004-IR.git
```

Unzip the 'yolov5.zip' folder in the same directory.

Create a new Conda environment:

```bash
conda create --name <env> --file requirements.txt
```

Open the ``paths.py`` file in the same directory and edit the following lines:

```python
# To get the absolute path of a file/directory, right click on it in Visual Studio Code and click on COPY PATH

# Change this to the absolute path of the directory .\runs\detect
image_folder = r'C:\Users\siddh\Desktop\mdp-g20\image_recognition\runs\detect'

# Change this to the absolute path of the directory ./yolov5/yolov5/content/yolov5
model_path = 'C:/Users/siddh/Desktop/mdp-g20/image_recognition/yolov5/content/yolov5' # local

# Change this to the absolute path of the directory ./best-model.pt
wts_path = 'C:/Users/siddh/Desktop/mdp-g20/image_recognition/best-model.pt'
```

To test all installations:

```bash
conda activate <env>
python test_model.py
```

(Ensure image2.jpg is in the same directory). The results of the YOLOv5 inference will be rendered on the screen successfully.

To run the server:

```bash
conda activate <env>
python imageserver.py
```

(Connect to the server as soon as your successfully connected to the WiFi - MDPGrp20) It takes about 30-40 seconds to instantiate the model and load it into the CUDA memory.
