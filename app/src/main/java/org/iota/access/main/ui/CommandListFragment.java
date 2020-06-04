/*
 *  This file is part of the IOTA Access distribution
 *  (https://github.com/iotaledger/access)
 *
 *  Copyright (c) 2020 IOTA Stiftung.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.iota.access.main.ui;

import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.iota.access.BaseActivity;
import org.iota.access.CommunicationFragment;
import org.iota.access.R;
import org.iota.access.api.model.Command;
import org.iota.access.api.model.token_server.TSSendRequest;
import org.iota.access.command_editor.CommandEditorFragment;
import org.iota.access.databinding.FragmentCommandListBinding;
import org.iota.access.di.AppSharedPreferences;
import org.iota.access.models.User;
import org.iota.access.user.UserManager;
import org.iota.access.utils.Optional;
import org.iota.access.utils.ui.DialogFragmentUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static org.iota.access.utils.Constants.TOKEN_SCALE_FACTOR;

/**
 * Fragment representing the Main Screen
 */
public class CommandListFragment extends CommunicationFragment<CommandListViewModel>
        implements CommandsAdapter.OnSelectedCommandListener {

    private final int ACTIVITY_RESULT_SPEECH = 10;
    private final String TAG_DIALOG_DELETE_COMMAND = "dialog_delete_command";
    private final String TAG_COMMAND_EDITOR = "command_editor_fragment";
    private final String TAG_CLEAR_POLICY = "clear_policy_dialog";
    @Inject
    public AppSharedPreferences mPreferences;
    @Inject
    public UserManager mUserManager;
    private FragmentCommandListBinding mBinding;
    private RecyclerView.Adapter mAdapter;
    private List<Command> mCommandList = new ArrayList<>();

    public static CommandListFragment newInstance() {
        return new CommandListFragment();
    }

    @Override
    protected Class<CommandListViewModel> getViewModelClass() {
        return CommandListViewModel.class;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mViewModel.isPolicyRequested())
            mViewModel.getPolicyList();
        else if (!mViewModel.getCommandList().isEmpty()) {
            mBinding.fab.show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_command_list, container, false);
        mBinding.commandList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.fab.setOnClickListener(__ -> onMicrophoneClick());
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mAdapter == null)
            mAdapter = new CommandsAdapter(mCommandList, this);
        mBinding.commandList.setAdapter(mAdapter);
        mBinding.swipeRefreshLayout.setOnRefreshListener(mViewModel::getPolicyList);

        combineCommandsFromServerAndFromPreferences();
    }

    public void combineCommandsFromServerAndFromPreferences() {
        List<Command> fromServer = null;
        if (!mViewModel.getCommandList().isEmpty())
            fromServer = mViewModel.getCommandList().get();

        List<Command> fromPreferences = mPreferences.getCommandList();
        List<Command> combinedList = new ArrayList<>();

        if (fromServer != null)
            combinedList.addAll(fromServer);

        if (fromPreferences != null)
            combinedList.addAll(fromPreferences);

        mCommandList.clear();
        mCommandList.addAll(combinedList);

        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void showSnackbar(String message) {
        super.showSnackbar(message);
        mBinding.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_clear_policy:
                clearPolicyList();
                return true;
            case R.id.action_add_new_command:
                Activity activity = getActivity();
                if (activity instanceof BaseActivity) {
                    ((BaseActivity) activity).addFragmentToBackStack(new CommandEditorFragment(), TAG_COMMAND_EDITOR);
                }
                return true;
            case R.id.action_refill_tokens:
                showDialogMessage(getString(R.string.msg_refill_tokens_question, String.valueOf((int) (CommandListViewModel.REFILL_AMOUNT * TOKEN_SCALE_FACTOR))), dialog -> refillAccount());
                return true;
            case R.id.action_clear_users:
                clearUserList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearPolicyList() {
        DialogFragment dialog = DialogFragmentUtil.createAlertDialog(
                getString(R.string.clear_policy),
                android.R.string.yes,
                android.R.string.no,
                __ -> mViewModel.clearPolicyList());
        DialogFragmentUtil.showDialog(dialog, getChildFragmentManager(), TAG_CLEAR_POLICY);
    }

    private void clearUserList() {
        DialogFragment dialog = DialogFragmentUtil.createAlertDialog(
                getString(R.string.clear_users),
                android.R.string.yes,
                android.R.string.no,
                __ -> mViewModel.clearUserList());
        DialogFragmentUtil.showDialog(dialog, getChildFragmentManager(), TAG_CLEAR_POLICY);
    }

    private void payForCommand(Command command) {
        User user = mUserManager.getUser();
        if (user == null)
            return;
        String senderWalletId = user.getWalletId();
        String receiverWalletId = getResources().getStringArray(R.array.wallet_ids)[0];
        int priority = 4;

        TSSendRequest requestBody = new TSSendRequest(senderWalletId,
                receiverWalletId,
                String.valueOf(command.getActiveAction().getCost()),
                priority);

        mViewModel.payPolicy(requestBody, command.getActiveAction().getPolicyId());
    }

    @Override
    public void onCommandSelected(Command command) {
        // check if command is NOT paid

        if (!command.getActiveAction().isPaid() && command.getActiveAction().getCost() != null) {
            Float cost = command.getActiveAction().getCost();
            String message = getString(R.string.msg_action_not_paid_question, String.valueOf(cost * TOKEN_SCALE_FACTOR));
            showDialogMessage(message, dialog -> payForCommand(command));
        } else {
            InputStream is = getClass().getResourceAsStream("/assets/template.json");

            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            mViewModel.executeCommand(command);
        }
    }

    @Override
    public void onCommandDeleted(Command command) {
        DialogFragment dialog = DialogFragmentUtil.createAlertDialog(
                getString(R.string.delete_command_question),
                android.R.string.yes,
                android.R.string.no,
                __ -> {
                    mPreferences.removeCommandFromList(command);
                    combineCommandsFromServerAndFromPreferences();
                });
        DialogFragmentUtil.showDialog(dialog, getChildFragmentManager(), TAG_DIALOG_DELETE_COMMAND);
    }

    @Override
    public void onMicrophoneClick() {
        startActivityForResult(mViewModel.getAsrIntent(), ACTIVITY_RESULT_SPEECH);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_RESULT_SPEECH) {
            if (resultCode == RESULT_OK && data != null) {
                mViewModel.onAsrResult(data);
            }
        }
    }

    private void showBackgroundMessage(@StringRes int message, @DrawableRes int messageImage) {
        mBinding.textMessage.setVisibility(View.VISIBLE);
        mBinding.textMessage.setText(message);
        mBinding.textMessage.setCompoundDrawablesWithIntrinsicBounds(0, messageImage, 0, 0);
    }

    private void hideBackgroundMessage() {
        mBinding.textMessage.setVisibility(View.GONE);
    }

    private void refillAccount() {
        if (mUserManager.getUser() == null) {
            return; // should newer occur
        }
        String walletId = mUserManager.getUser().getWalletId();
        mViewModel.refillAccount(walletId);
    }

    private void showRefresh(boolean flag) {
        mBinding.swipeRefreshLayout.setRefreshing(flag);
    }

    private void handleNewCommandList(List<Command> commandList) {
        combineCommandsFromServerAndFromPreferences();

        if (commandList.isEmpty()) {
            mBinding.fab.hide();
            showBackgroundMessage(R.string.msg_no_commands, R.drawable.ic_delegate);
        } else {
            mBinding.fab.show();
            hideBackgroundMessage();
        }
    }

    @Override
    protected void bindViewModel() {
        super.bindViewModel();

        // subscribe for changes on command list
        // List is wrapped in Optional, because list can be null (nothing has been received - invalid response),
        // or list can contain elements (empty list is handled like valid response)
        mDisposable.add(mViewModel
                .getObservableCommandList()
                .observeOn(AndroidSchedulers.mainThread())
                .filter(listOptional -> !listOptional.isEmpty())
                .map(Optional::get)
                .subscribe(
                        // on next
                        this::handleNewCommandList,
                        // on error
                        Timber::e));

        // subscribe for when to show refresh
        mDisposable.add(mViewModel
                .getShowRefresh()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // on next
                        this::showRefresh,
                        // on error
                        Timber::e));
    }
}