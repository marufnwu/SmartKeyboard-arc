# Copyright (C) 2011 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
LOCAL_PATH := $(call my-dir)
############ some local flags
# If you change any of those flags, you need to rebuild both libjni_latinime_common_static
# and the shared library that uses libjni_latinime_common_static.
FLAG_DBG ?= false
FLAG_DO_PROFILE ?= false
######################################
include $(CLEAR_VARS)
LATIN_IME_SRC_DIR := src
LATIN_IME_SRC_FULLPATH_DIR := $(LOCAL_PATH)/$(LATIN_IME_SRC_DIR)
LOCAL_C_INCLUDES += $(LATIN_IME_SRC_FULLPATH_DIR) $(LATIN_IME_SRC_FULLPATH_DIR)/gesture
LOCAL_CFLAGS += -Werror -Wall
# To suppress compiler warnings for unused variables/functions used for debug features etc.
LOCAL_CFLAGS += -Wno-unused-parameter -Wno-unused-function
LATIN_IME_JNI_SRC_FILES := \
    com_android_inputmethod_keyboard_ProximityInfo.cpp \
    com_android_inputmethod_latin_BinaryDictionary.cpp \
    com_android_inputmethod_latin_DicTraverseSession.cpp \
    com_android_inputmethod_latin_BinaryDictionaryUtils.cpp \
    jni_common.cpp
LATIN_IME_CORE_SRC_FILES := \
	dictionary\header\header_policy.cpp \
	dictionary\header\header_read_write_utils.cpp \
	dictionary\property\ngram_context.cpp \
	dictionary\structure\dictionary_structure_with_buffer_policy_factory.cpp \
	dictionary\structure\pt_common\bigram\bigram_list_read_write_utils.cpp \
	dictionary\structure\pt_common\dynamic_pt_gc_event_listeners.cpp \
	dictionary\structure\pt_common\dynamic_pt_reading_helper.cpp \
	dictionary\structure\pt_common\dynamic_pt_reading_utils.cpp \
	dictionary\structure\pt_common\dynamic_pt_updating_helper.cpp \
	dictionary\structure\pt_common\dynamic_pt_writing_utils.cpp \
	dictionary\structure\pt_common\patricia_trie_reading_utils.cpp \
	dictionary\structure\pt_common\shortcut\shortcut_list_reading_utils.cpp \
	dictionary\structure\v2\patricia_trie_policy.cpp \
	dictionary\structure\v2\ver2_patricia_trie_node_reader.cpp \
	dictionary\structure\v2\ver2_pt_node_array_reader.cpp \
	dictionary\structure\v4\ver4_dict_buffers.cpp \
	dictionary\structure\v4\ver4_dict_constants.cpp \
	dictionary\structure\v4\ver4_patricia_trie_node_reader.cpp \
	dictionary\structure\v4\ver4_patricia_trie_node_writer.cpp \
	dictionary\structure\v4\ver4_patricia_trie_policy.cpp \
	dictionary\structure\v4\ver4_patricia_trie_reading_utils.cpp \
	dictionary\structure\v4\ver4_patricia_trie_writing_helper.cpp \
	dictionary\structure\v4\ver4_pt_node_array_reader.cpp \
	dictionary\structure\v4\content\dynamic_language_model_probability_utils.cpp \
	dictionary\structure\v4\content\language_model_dict_content.cpp \
	dictionary\structure\v4\content\language_model_dict_content_global_counters.cpp \
	dictionary\structure\v4\content\shortcut_dict_content.cpp \
	dictionary\structure\v4\content\sparse_table_dict_content.cpp \
	dictionary\structure\v4\content\terminal_position_lookup_table.cpp \
	dictionary\utils\buffer_with_extendable_buffer.cpp \
	dictionary\utils\byte_array_utils.cpp \
	dictionary\utils\dict_file_writing_utils.cpp \
	dictionary\utils\file_utils.cpp \
	dictionary\utils\forgetting_curve_utils.cpp \
	dictionary\utils\format_utils.cpp \
	dictionary\utils\mmapped_buffer.cpp \
	dictionary\utils\multi_bigram_map.cpp \
	dictionary\utils\probability_utils.cpp \
	dictionary\utils\sparse_table.cpp \
	dictionary\utils\trie_map.cpp \
	suggest\core\suggest.cpp \
	suggest\core\dicnode\dic_node.cpp \
	suggest\core\dicnode\dic_node_utils.cpp \
	suggest\core\dicnode\dic_nodes_cache.cpp \
	suggest\core\dictionary\dictionary.cpp \
	suggest\core\dictionary\dictionary_utils.cpp \
	suggest\core\dictionary\digraph_utils.cpp \
	suggest\core\dictionary\error_type_utils.cpp \
	suggest\core\layout\additional_proximity_chars.cpp \
	suggest\core\layout\proximity_info.cpp \
	suggest\core\layout\proximity_info_params.cpp \
	suggest\core\layout\proximity_info_state.cpp \
	suggest\core\layout\proximity_info_state_utils.cpp \
	suggest\core\policy\weighting.cpp \
	suggest\core\session\dic_traverse_session.cpp \
	suggest\core\result\suggestion_results.cpp \
	suggest\core\result\suggestions_output_utils.cpp \
	suggest\policyimpl\gesture\gesture_suggest_policy_factory.cpp \
	suggest\policyimpl\typing\scoring_params.cpp \
	suggest\policyimpl\typing\typing_scoring.cpp \
	suggest\policyimpl\typing\typing_suggest_policy.cpp \
	suggest\policyimpl\typing\typing_traversal.cpp \
	suggest\policyimpl\typing\typing_weighting.cpp \
	utils\autocorrection_threshold_utils.cpp \
	utils\char_utils.cpp \
	utils\jni_data_utils.cpp \
	utils\log_utils.cpp \
	utils\time_keeper.cpp \
	dictionary\structure\backward\v402\ver4_dict_buffers.cpp \
	dictionary\structure\backward\v402\ver4_dict_constants.cpp \
	dictionary\structure\backward\v402\ver4_patricia_trie_node_reader.cpp \
	dictionary\structure\backward\v402\ver4_patricia_trie_node_writer.cpp \
	dictionary\structure\backward\v402\ver4_patricia_trie_policy.cpp \
	dictionary\structure\backward\v402\ver4_patricia_trie_reading_utils.cpp \
	dictionary\structure\backward\v402\ver4_patricia_trie_writing_helper.cpp \
	dictionary\structure\backward\v402\ver4_pt_node_array_reader.cpp \
	dictionary\structure\backward\v402\content\bigram_dict_content.cpp \
	dictionary\structure\backward\v402\content\probability_dict_content.cpp \
	dictionary\structure\backward\v402\content\shortcut_dict_content.cpp \
	dictionary\structure\backward\v402\content\sparse_table_dict_content.cpp \
	dictionary\structure\backward\v402\content\terminal_position_lookup_table.cpp \
	dictionary/structure/backward/v402/bigram/ver4_bigram_list_policy.cpp \
LOCAL_SRC_FILES := \
    $(LATIN_IME_JNI_SRC_FILES) \
    $(addprefix $(LATIN_IME_SRC_DIR)/, $(LATIN_IME_CORE_SRC_FILES))
ifeq ($(FLAG_DO_PROFILE), true)
    $(warning Making profiling version of native library)
    LOCAL_CFLAGS += -DFLAG_DO_PROFILE
else # FLAG_DO_PROFILE
ifeq ($(FLAG_DBG), true)
    $(warning Making debug version of native library)
    LOCAL_CFLAGS += -DFLAG_DBG
endif # FLAG_DBG
endif # FLAG_DO_PROFILE
LOCAL_MODULE := libjni_latinime_common_static
LOCAL_MODULE_TAGS := optional
LOCAL_SDK_VERSION := 33
LOCAL_NDK_STL_VARIANT := stlport_static
include $(BUILD_STATIC_LIBRARY)
######################################
include $(CLEAR_VARS)
# All code in LOCAL_WHOLE_STATIC_LIBRARIES will be built into this shared library.
LOCAL_WHOLE_STATIC_LIBRARIES := libjni_latinime
ifeq ($(FLAG_DO_PROFILE), true)
    $(warning Making profiling version of native library)
    LOCAL_SHARED_LIBRARIES += liblog
else # FLAG_DO_PROFILE
ifeq ($(FLAG_DBG), true)
    $(warning Making debug version of native library)
    LOCAL_SHARED_LIBRARIES += liblog
endif # FLAG_DBG
endif # FLAG_DO_PROFILE
LOCAL_MODULE := libjni_latinime
LOCAL_MODULE_TAGS := optional
LOCAL_SDK_VERSION := 14
LOCAL_NDK_STL_VARIANT := stlport_static
LOCAL_LDFLAGS += -ldl
include $(BUILD_SHARED_LIBRARY)
#################### Clean up the tmp vars
LATIN_IME_CORE_SRC_FILES :=
LATIN_IME_JNI_SRC_FILES :=
LATIN_IME_GESTURE_IMPL_SRC_FILES :=
LATIN_IME_SRC_DIR :=
LATIN_IME_SRC_FULLPATH_DIR :=