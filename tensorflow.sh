# bazel https://docs.bazel.build/versions/master/install-ubuntu.html#install-on-ubuntu

./configure
# build the retrainer
bazel build --config opt tensorflow/examples/image_retraining:retrain
# build label_image
bazel build --config opt tensorflow/examples/image_retraining:label_image

# run retrainer
bazel-bin/tensorflow/examples/image_retraining/retrain --image_dir ~/projects/q/butterfly-vn/data
# visualize at http://localhost:6006
tensorboard --logdir /tmp/retrain_logs

cp /tmp/output_* ~/projects/q/butterfly-vn/

# run with log disable (value 0->3)
TF_CPP_MIN_LOG_LEVEL=3 \
bazel-bin/tensorflow/examples/image_retraining/label_image \
--graph=/tmp/output_graph.pb --labels=/tmp/output_labels.txt \
--output_layer=final_result:0 \
--image=$HOME/data/231s.jpg

# todo
--how_many_training_steps default 4000 -> 8000
--random_crop, --random_scale and --random_brightness: percentage values, start with values of 5 or 10 for each
--flip_left_right