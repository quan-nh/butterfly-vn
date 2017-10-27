# bazel
# https://docs.bazel.build/versions/master/install-ubuntu.html#install-on-ubuntu
# https://docs.bazel.build/versions/master/install-os-x.html#install-on-mac-os-x-homebrew
cd ~/projects/github/tensorflow/
./configure
bazel build --config opt tensorflow/examples/image_retraining:retrain
bazel build --config opt tensorflow/examples/image_retraining:label_image

# retrain
bazel-bin/tensorflow/examples/image_retraining/retrain --how_many_training_steps 8000 --random_crop 5 --random_scale 5 --random_brightness 5 --image_dir ~/projects/q/butterfly-vn/data-train

# visualize at http://localhost:6006
tensorboard --logdir /tmp/retrain_logs

cp /tmp/output_* ~/projects/q/butterfly-vn/tf/

# test
TF_CPP_MIN_LOG_LEVEL=3 \
bazel-bin/tensorflow/examples/image_retraining/label_image \
--graph=$HOME/projects/q/butterfly-vn/tf/output_graph.pb --labels=$HOME/projects/q/butterfly-vn/tf/output_labels.txt \
--image=$HOME/data/231s.jpg
